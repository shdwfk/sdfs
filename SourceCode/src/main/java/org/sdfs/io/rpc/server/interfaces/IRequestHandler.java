package org.sdfs.io.rpc.server.interfaces;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcMessage;

/**
 * Request处理，并写回Response对象
 * @author wangfk
 *
 */
public interface IRequestHandler extends ChannelInboundHandler {
	public void addResponse(ChannelHandlerContext context,
			RpcMessage<IResponse> response) throws Exception;

	public void setRequestExecutor(IRequestExecutor requestExecutor);
}
