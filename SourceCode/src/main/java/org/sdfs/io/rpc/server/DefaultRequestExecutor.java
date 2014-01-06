package org.sdfs.io.rpc.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.RpcMessage;
import org.sdfs.io.rpc.server.interfaces.IRequestExecutor;
import org.sdfs.io.rpc.server.interfaces.IRequestHandler;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;

/**
 * 基于多线程的request执行器，request由队列缓存并交由线程执行。
 * request的实际处理由{@link IRequestInvokeAdaptor}进行
 * @author wangfk
 *
 */
public class DefaultRequestExecutor implements IRequestExecutor {
	class Request {
		private IRequestHandler handler;
		private ChannelHandlerContext context;
		private RpcMessage<IRequest> request;

		public Request(IRequestHandler handler, ChannelHandlerContext context,
				RpcMessage<IRequest> request) {
			this.handler = handler;
			this.context = context;
			this.request = request;
		}

		public IRequestHandler getHandler() {
			return handler;
		}
		public RpcMessage<IRequest> getRequest() {
			return request;
		}
		public ChannelHandlerContext getContext() {
			return context;
		}
	}

	private static final int REQUEST_QUEUE_LENGTH = 1000; //TODO config
	private BlockingQueue<Request> requestQueue =
			new ArrayBlockingQueue<Request>(REQUEST_QUEUE_LENGTH);
	
	private ThreadGroup requestExecuteThreadGroup = new ThreadGroup("RpcRequestExecutor");
	private RequestExecuteThread[] threads;
	private IRequestInvokeAdaptor adaptor;
	private volatile boolean stopped;

	public DefaultRequestExecutor(int concurrency) {
		threads = new RequestExecuteThread[concurrency];
		for (int i = 0; i < concurrency; ++ i) {
			threads[i] = new RequestExecuteThread(i);
		}
		for (RequestExecuteThread thread : threads) {
			thread.start();
		}
	}

	@Override
	public void addNewRequest(IRequestHandler handler, ChannelHandlerContext context,
			RpcMessage<IRequest> request) throws RpcException {
		if (stopped) {
			//TODO exception type?
			throw new RpcException("The service is stopped.");
		}
		try {
			requestQueue.add(new Request(handler, context, request));
		} catch (IllegalStateException e) {
			throw new RpcException("The rpc request queue is full, queue size="
					+ REQUEST_QUEUE_LENGTH, e);
		}
	}

	@Override
	public int getRequestQueueSize() {
		return requestQueue.size();
	}

	@Override
	public void setRequestInvokeAdaptor(IRequestInvokeAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	@Override
	public void stop() {
		this.stopped = true;
		for (RequestExecuteThread thread : threads) {
			thread.interrupt();
		}
	}

	@Override
	public void join() throws InterruptedException {
		for (RequestExecuteThread thread : threads) {
			thread.join();
		}
	}

	private class RequestExecuteThread extends Thread {

		public RequestExecuteThread(int index) {
			super(requestExecuteThreadGroup, "Thread-" + index);
			super.setDaemon(true);
		}

		@Override
		public void run() {
			while (!stopped) {
				Request request;
				try {
					request = requestQueue.take();
				} catch (InterruptedException e) {
						//TODO logging
						continue;
				}

				RpcMessage<IResponse> responseMessage = null;
				RpcMessage<IRequest> requestMessage = request.getRequest();
				try {
					// 调用adaptor中的方法去执行request，并等待response
					IResponse response = adaptor.invokeRequest(
							requestMessage.getMessage());
					responseMessage = new RpcMessage<IResponse>(
							requestMessage.getMessageId(), response);
				} catch (Exception e) {
					//如果request执行失败，将exception发回给客户端
					responseMessage = new RpcMessage<IResponse>(
							requestMessage.getMessageId(), new RpcException(e));
				}

				try {
					request.getHandler().addResponse(
								request.getContext(), responseMessage);
				} catch (Exception e) {
					// rpc调用失败，则不能将exception发回客户端，服务器端消化exception
					// 因为本身就是IO失败，再通过IO发送exception
					// 一则不能保证客户端收到，二则可能会加剧网络问题
					//TODO logging
				}
			}
			if (!stopped) {
				//在没有主动stop的情况下，线程退出，说明有严重的异常发生
				//TODO log serious event
			}
		}
	}
}
