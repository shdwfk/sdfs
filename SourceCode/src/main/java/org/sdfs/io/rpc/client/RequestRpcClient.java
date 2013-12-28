package org.sdfs.io.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

import org.sdfs.io.SdfsSerializationHelper;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcMessage;

/**
 * Channel Outbound：序列化Request
 * @author wangfk
 *
 */
class RequestSendHandler extends MessageToByteEncoder<RpcMessage<IRequest>> {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage<IRequest> request, ByteBuf out)
			throws Exception {
		SdfsSerializationHelper.writeObject(new ByteBufOutputStream(out), request);
	}
}

/**
 * Channel Inbound：反序列化Response
 * @author wangfk
 *
 */
class ResponseReceiveHandler extends ChannelInboundHandlerAdapter {
	private int dataLen = -1;
	private ByteBuf buffer;

	private RpcMessage<IResponse> response;

    public RpcMessage<IResponse> getResponse() {
		return response;
	}

	@Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    	buffer = ctx.alloc().buffer();
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    	buffer.release();
    	buffer = null;
    }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		if (dataLen == -1) {
			if (buf.readableBytes() < 4) {
				return;
			}
			dataLen = buf.readInt();
			buffer.capacity(dataLen);
		}
		buffer.writeBytes(buf);
		if (buffer.readableBytes() >= dataLen) {
			response = SdfsSerializationHelper.readObjectWithoutLen(new ByteBufInputStream(buffer));
			//一旦response接受完毕，就关闭channel
			ctx.close();
		}
	}
}

public class RequestRpcClient {
	
	public static RpcMessage<IResponse> requestInvoke(String host, int port, final RpcMessage<IRequest> request) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ResponseReceiveHandler responseReceiveHandler = new ResponseReceiveHandler();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new RequestSendHandler(), responseReceiveHandler);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(request);

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
        //返回handler解析的response对象
		return responseReceiveHandler.getResponse();
	}
}
