package org.sdfs.io.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

import org.sdfs.io.ISdfsSerializable;
import org.sdfs.io.SdfsSerializationHelper;

/**
 * Channel Outbound：RpcMessage序列化，RpcMessage --> ByteBuf（数据流）<br>
 * 数据流格式：|对象长度(int)|对象类型(int)|对象序列化数据|
 * @author wangfk
 *
 */
public class RpcMessageEncoder<T extends ISdfsSerializable> extends MessageToByteEncoder<List<RpcMessage<T>>> {

	@Override
	protected void encode(ChannelHandlerContext ctx, List<RpcMessage<T>> responseList, ByteBuf out)
			throws Exception {
		ByteBufOutputStream bbos = new ByteBufOutputStream(out);
		try {
			for (RpcMessage<T> response : responseList) {
				SdfsSerializationHelper.writeObject(bbos, response);
			}
		} finally {
			bbos.close();
		}
	}
}
