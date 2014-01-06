package org.sdfs.io.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.RpcMessage;

/**
 * 发送request，接收response
 * @author wangfk
 *
 */
class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessage<IResponse>> {

	class RpcCallFuture implements Future<IResponse> {
		//TODO max wait time
		private boolean cancelled;

		private long messageId;
		private IRequest request;
		
		private long createTimeMillis;

		private IResponse response;
		private RpcException exception;

		public RpcCallFuture(long messageId, IRequest request) {
			this.messageId = messageId;
			this.request = request;
			this.createTimeMillis = System.currentTimeMillis();
		}

		/**
		 * 设置执行结果：Server端返回的response
		 * 并notify所有在future上等待的线程
		 * @param response
		 */
		public void setResponse(IResponse response) {
			this.response = response;
			synchronized (this) {
				this.notifyAll();
			}
		}

		/**
		 * 设置执行结果：Server端返回的exception
		 * 并notify所有在future上等待的线程
		 * @param exception
		 */
		public void setException(RpcException exception) {
			this.exception = exception;
			synchronized (this) {
				this.notifyAll();
			}
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			// request 尚未提交，可以取消
			synchronized (this) {
				if (request != null) {
					request = null;
					cancelled = true;
					return true;
				}
			}

			// 已经取消了，直接返回取消成功
			if (cancelled) {
				return true;
			}
			// 如果已经有了执行结果，无法取消
			if (response != null || exception != null) {
				return false;
			}
			// 尝试从未发送的request缓存区中删除待发送的request
			if (removeRequestFromBuffer(messageId) != null) {
				cancelled = true;
				synchronized (this) {
					this.notifyAll();
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public boolean isDone() {
			return cancelled || response != null || exception != null;
		}

		@Override
		public IResponse get() throws InterruptedException, ExecutionException {
			try {
				return get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				throw new ExecutionException(
						"The call time exceeds the upper limit.", e);
			}
		}

		@Override
		public IResponse get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			long timeoutMillis = Math.min(callTimeOutMillis, unit.toMillis(timeout));

			// 检查是否超时
			long currentTimeout = getRemainTimeMillis(timeoutMillis);
			if (currentTimeout <= 0) {
				throw new TimeoutException();
			}

			// 将request放到发送缓冲区
			synchronized (this) {
				if (request != null) {
					try {
						addRequestToBuffer(new RpcMessage<>(messageId, request));
					} catch (RpcException e) {
						throw new ExecutionException(e);
					}
					request = null;
				}
			}

			while (true) {
				// synchronized 将cancelled、response、exception等的条件判断语句都包含进来
				// 否则：某个条件语句执行结束，但wait语句尚未执行时，
				// 如果另外一个线程改变了状态使得该条件判断语句不成立，并做了notify操作，
				// wait语句将会等到超时为止
				synchronized (this) {
					// 请求已经被取消，返回InterruptedException
					if (cancelled) {
						throw new InterruptedException("The call was cancelled");
					}
					// 已经有执行结果，返回结果
					if (response != null) {
						return response;
					}
					// 执行出现问题，抛exception
					if (exception != null) {
						throw new ExecutionException(exception);
					}
	
					// 重新计算超时
					currentTimeout = getRemainTimeMillis(timeoutMillis);
					if (currentTimeout <= 0) {
						throw new TimeoutException();
					}
	
					// 在超时时间内等待通知
					this.wait(currentTimeout);
				}
			}
		}

		private long getRemainTimeMillis(long timeoutMillis) {
			return createTimeMillis + timeoutMillis - System.currentTimeMillis();
		}
	}

	private long callTimeOutMillis;
	private int maxRequestBufferSize;
	private long cleanCallFutureIntervalMills;

	private LinkedHashMap<Long, RpcCallFuture> rpcCallFutureMap = new LinkedHashMap<>();
	private ReentrantLock rpcCallFutureMapLock = new ReentrantLock();

	private List<RpcMessage<IRequest>> requestBuffer = new ArrayList<>();
	private ReentrantLock requestBufferLock = new ReentrantLock();

	private AtomicLong messageId = new AtomicLong(0);

	private ChannelHandlerContext context;

	/**
	 * RPC调用等待超时时长
	 * @param callTimeOutMillis
	 * @return
	 */
	public RpcClientHandler setCallTimeOutMillis(long callTimeOutMillis) {
		this.callTimeOutMillis = callTimeOutMillis;
		return this;
	}

	/**
	 * RPC request缓存大小
	 * @param maxRequestBufferSize
	 * @return
	 */
	public RpcClientHandler setMaxRequestBufferSize(int maxRequestBufferSize) {
		this.maxRequestBufferSize = maxRequestBufferSize;
		return this;
	}

	/**
	 * Call Future清理间隔
	 * @param cleanCallFutureIntervalMills
	 * @return
	 */
	public RpcClientHandler setCleanCallFutureIntervalMills(
			long cleanCallFutureIntervalMills) {
		this.cleanCallFutureIntervalMills = cleanCallFutureIntervalMills;
		return this;
	}

	/**
	 * 返回一个future，调用{@link Future#get} 方法，request才真正被发送
	 * @param request
	 * @return
	 */
	public Future<IResponse> addNewRequest(IRequest request) {
		long id = messageId.getAndDecrement();
		RpcCallFuture future = new RpcCallFuture(id, request);
		rpcCallFutureMapLock.lock();
		try {
			rpcCallFutureMap.put(id, future);
		} finally {
			rpcCallFutureMapLock.unlock();
		}
		return future;
	}

	/**
	 * 将一个request消息放到发送缓冲区
	 * @param rpcMessage
	 * @throws RpcException
	 */
	private void addRequestToBuffer(RpcMessage<IRequest> rpcMessage) throws RpcException {
		requestBufferLock.lock();
		try {
			if (requestBuffer.size() > maxRequestBufferSize) {
				throw new RpcException("The request buffer is exceeded.");
			}
			requestBuffer.add(rpcMessage);
		} finally {
			requestBufferLock.unlock();
		}

		context.executor().execute(new Runnable() {
			
			@Override
			public void run() {
				if (context.channel().isWritable()) {
					writeAndFlushBuffer(context);
				}
			}
		} );
	}

	private void cleanRpcCallFutureMap() {
		rpcCallFutureMapLock.lock();
		try {
			Iterator<Entry<Long, RpcCallFuture>> iterator =
					rpcCallFutureMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Long, RpcCallFuture> entry = iterator.next();
				RpcCallFuture future = entry.getValue();
				if (future.getRemainTimeMillis(callTimeOutMillis) > 0) {
					break;
				} else {
					if (future.cancel(true) || future.isDone()) {
						// 取消成功、已取消、已完成
						iterator.remove();
					} else {
						future.setException(new RpcException(
								"The future was cleaned from the buffer" +
								" because of no response and timeout."));
					}
				}
			}
		} finally {
			rpcCallFutureMapLock.unlock();
		}
	}

	/**
	 * 根据request message的id，尝试从缓冲区中删除该消息
	 * @param messageId
	 * @return
	 */
	private RpcMessage<IRequest> removeRequestFromBuffer(long messageId) {
		requestBufferLock.lock();
		try {
			for (int i = 0; i < requestBuffer.size(); ++ i) {
				RpcMessage<IRequest> rpcMessage = requestBuffer.get(i);
				if (rpcMessage.getMessageId() == messageId) {
					return requestBuffer.remove(i);
				}
			}
		} finally {
			requestBufferLock.unlock();
		}
		return null;
	}

	private void writeAndFlushBuffer(ChannelHandlerContext ctx) {
		List<RpcMessage<IRequest>> tempBuffer = null;
		requestBufferLock.lock();
		try {
			if (requestBuffer.isEmpty()) {
				return;
			}
			tempBuffer = requestBuffer;
			requestBuffer = new ArrayList<>();
		} finally {
			requestBufferLock.unlock();
		}
		ctx.writeAndFlush(tempBuffer);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		this.context = ctx;
		ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				cleanRpcCallFutureMap();
			}
		}, cleanCallFutureIntervalMills, cleanCallFutureIntervalMills, TimeUnit.MILLISECONDS);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		if (ctx.channel().isWritable()) {
			writeAndFlushBuffer(ctx);
		}
		super.channelWritabilityChanged(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			RpcMessage<IResponse> msg) throws Exception {
		RpcCallFuture future = null;
		// 读取request执行结果（response或者exception）
		// 根据messageId找到对应的Future
		rpcCallFutureMapLock.lock();
		try {
			future = rpcCallFutureMap.remove(msg.getMessageId());
		} finally {
			rpcCallFutureMapLock.unlock();
		}
		if (future == null) {
			//TODO error log
			return;
		}
		// 将执行结果set到Future中
		if (msg.hasException()) {
			future.setException(msg.getException());
		} else {
			future.setResponse(msg.getMessage());
		}
	}
	
}