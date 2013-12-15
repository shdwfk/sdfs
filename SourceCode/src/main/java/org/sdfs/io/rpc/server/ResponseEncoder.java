package org.sdfs.io.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.sdfs.io.SdfsSerializationHelper;
import org.sdfs.io.response.IResponse;

/**
 * Channel Outbound：Response序列化，Response --> ByteBuf（数据流）<br>
 * 数据流格式：|对象长度(int)|对象类型(int)|对象序列化数据|
 * @author wangfk
 *
 */
public class ResponseEncoder extends MessageToByteEncoder<IResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IResponse msg, ByteBuf out)
			throws Exception {
		System.out.println("ResponseEncoder: Method invoked: encode, msg: " + msg);
		ByteBufOutputStream bbos = new ByteBufOutputStream(out);
		SdfsSerializationHelper.writeObject(bbos, msg);
		bbos.flush();
	}
}
