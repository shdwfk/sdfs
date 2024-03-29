package org.sdfs.io.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.sdfs.io.ISdfsSerializable;
import org.sdfs.io.SdfsSerializationHelper;

/**
 * 记录数据解析状态
 * @author wangfk
 *
 */
enum DecoderState {
	   READ_LENGTH,
	   READ_CONTENT,
	 };

	 
/**
 * Channel Inbound：RpcMassage反序列化， ByteBuf（数据流） --> RpcMassage对象<br>
 * 数据流格式：|对象长度(int)|对象类型(int)|对象序列化数据|
 * 
 * @author wangfk
 *
 */
public class RpcMessageDecoder<T extends ISdfsSerializable> extends
		ReplayingDecoder<DecoderState> {
	private int length;

	public RpcMessageDecoder() {
		super(DecoderState.READ_LENGTH);
		setSingleDecode(false);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		switch (state()) {
		case READ_LENGTH:
			// 读取对象长度
			length = in.readInt();
			checkpoint(DecoderState.READ_CONTENT);
			break;
		case READ_CONTENT:
			//根据对象长度，读取数据并反序列化Request对象
			ByteBuf data = in.readBytes(length);
			checkpoint(DecoderState.READ_LENGTH);
			RpcMessage<T> request =
					SdfsSerializationHelper.readObjectWithoutLen(data.array());
			out.add(request);
			break;
		default:
			break;
		}
	}
}
