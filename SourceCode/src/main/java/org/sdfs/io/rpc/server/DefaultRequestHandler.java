package org.sdfs.io.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcMessage;
import org.sdfs.io.rpc.server.interfaces.IRequestExecutor;
import org.sdfs.io.rpc.server.interfaces.IRequestHandler;

/**
 * {@link IRequestHandler}的默认实现
 * @author wangfk
 *
 */
public class DefaultRequestHandler extends SimpleChannelInboundHandler<RpcMessage<IRequest>> implements IRequestHandler {
	private static final int MAX_RESPONSE_BUFFER_SIZE = 16;

	private IRequestExecutor requestExecutor;
	private List<RpcMessage<IResponse>> responseBuffer =
			new ArrayList<RpcMessage<IResponse>>(MAX_RESPONSE_BUFFER_SIZE);
	private ReentrantLock responseBufferLock = new ReentrantLock();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<IRequest> request)
			throws Exception {
		requestExecutor.addNewRequest(this, ctx, request);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		if (ctx.channel().isWritable()) {
			writeAndFlushBuffer(ctx);
		}
		//ctx.fireChannelWritabilityChanged();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO rewrite this method
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void addResponse(final ChannelHandlerContext context, RpcMessage<IResponse> response) {
		responseBufferLock.lock();
		try {
			if (MAX_RESPONSE_BUFFER_SIZE > responseBuffer.size()) {
				responseBuffer.add(response);
			} else {
				//TODO error handler, throw some exception
			}
		} finally {
			responseBufferLock.unlock();
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

	@Override
	public void setRequestExecutor(IRequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor; 
	}

	private void writeAndFlushBuffer(ChannelHandlerContext ctx) {
		List<RpcMessage<IResponse>> tempBuffer = null;
		responseBufferLock.lock();
		try {
			if (responseBuffer.isEmpty()) {
				return;
			}
			tempBuffer = responseBuffer;
			responseBuffer = new ArrayList<RpcMessage<IResponse>>(MAX_RESPONSE_BUFFER_SIZE);
		} finally {
			responseBufferLock.unlock();
		}

		ctx.writeAndFlush(tempBuffer);
	}
}
