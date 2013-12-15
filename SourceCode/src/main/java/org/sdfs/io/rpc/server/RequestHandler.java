package org.sdfs.io.rpc.server;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.AddNewFileResponse;
import org.sdfs.io.response.IResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Request处理，生成Response对象，并写回
 * @author wangfk
 *
 */
public class RequestHandler extends SimpleChannelInboundHandler<IRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IRequest request)
			throws Exception {
		IResponse response = handleRequest(request);
		ctx.write(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * 处理用户数据，目前这里是demo实现，仅打印request，构造并返回了response
	 * @param request
	 * @return
	 */
	protected IResponse handleRequest(IRequest request) {
		System.out.println(request);
		AddNewFileResponse addNewFileResponse = new AddNewFileResponse();
		addNewFileResponse.setSuccessful(true);
		return addNewFileResponse;
	}
}
