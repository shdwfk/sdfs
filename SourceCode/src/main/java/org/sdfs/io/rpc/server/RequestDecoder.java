package org.sdfs.io.rpc.server;

import java.util.List;

import org.sdfs.io.SdfsSerializationHelper;
import org.sdfs.io.request.IRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;


/**
 * 记录数据解析状态
 * @author wangfk
 *
 */
enum RequestDecoderState {
	   READ_LENGTH,
	   READ_CONTENT,
	 };

/**
 * Channel Inbound：Request反序列化， ByteBuf（数据流） --> Request对象<br>
 * 数据流格式：|对象长度(int)|对象类型(int)|对象序列化数据|
 * @author wangfk
 *
 */
public class RequestDecoder extends ReplayingDecoder<RequestDecoderState> {
	private int length;

	public RequestDecoder() {
		super(RequestDecoderState.READ_LENGTH);
		setSingleDecode(false);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		System.out.println("RequestDecoder: Method invoked: decode");
		switch (state()) {
		case READ_LENGTH:
			// 读取对象长度
			length = in.readInt();
			checkpoint(RequestDecoderState.READ_CONTENT);
			break;
		case READ_CONTENT:
			//根据对象长度，读取数据并反序列化Request对象
			ByteBuf data = in.readBytes(length);
			checkpoint(RequestDecoderState.READ_LENGTH);
			IRequest request = SdfsSerializationHelper.readObjectWithoutLen(data.array());
			System.out.println("RequestDecoder: " + request);
			out.add(request);
			break;
		default:
			break;
		}
	}

}
