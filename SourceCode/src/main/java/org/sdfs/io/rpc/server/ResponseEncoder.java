package org.sdfs.io.rpc.server;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.sdfs.io.SdfsSerializationHelper;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcMessage;

/**
 * Channel Outbound：Response序列化，Response --> ByteBuf（数据流）<br>
 * 数据流格式：|对象长度(int)|对象类型(int)|对象序列化数据|
 * @author wangfk
 *
 */
public class ResponseEncoder extends MessageToByteEncoder<List<RpcMessage<IResponse>>> {

	@Override
	protected void encode(ChannelHandlerContext ctx, List<RpcMessage<IResponse>> responseList, ByteBuf out)
			throws Exception {
		ByteBufOutputStream bbos = new ByteBufOutputStream(out);
		try {
			for (RpcMessage<IResponse> response : responseList) {
				SdfsSerializationHelper.writeObject(bbos, response);
			}
		} finally {
			bbos.close();
		}
	}
}
