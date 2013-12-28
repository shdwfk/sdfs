package org.sdfs.io.rpc.server.interfaces;

import io.netty.channel.ChannelHandlerContext;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.RpcMessage;

/**
 * Request的执行器
 * @author wangfk
 *
 */
public interface IRequestExecutor {
	public void addNewRequest(IRequestHandler handler, ChannelHandlerContext context,
			RpcMessage<IRequest> request) throws RpcException;

	public int getRequestQueueSize();

	public void setRequestInvokeAdaptor(IRequestInvokeAdaptor adaptor);

	public void stop();

	public void join() throws InterruptedException;
}
