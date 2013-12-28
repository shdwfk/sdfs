package org.sdfs.io.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.ISdfsSerializable;
import org.sdfs.io.SdfsSerializationRegistry;

/**
 * 在request和response之外包装了message id，从而支持数据批量发送与异步获取
 * RpcMessage(messageId, message): |messageId(long)|messageRegistType(int)|message content|
 * @author wangfk
 *
 * @param <T>
 */
public class RpcMessage<T extends ISdfsSerializable> implements ISdfsSerializable {
	private long messageId;
	private T message;
	private RpcException exception;

	public RpcMessage(long messageId, T message) {
		this.messageId = messageId;
		this.message = message;
	}

	public RpcMessage(long messageId, RpcException exception) {
		this.messageId = messageId;
		this.exception = exception;
	}

	public RpcMessage() {
	}

	public long getMessageId() {
		return messageId;
	}

	public T getMessage() {
		return message;
	}

	public RpcException getException() {
		return exception;
	}

	public boolean hasException() {
		return exception != null;
	}

	@SuppressWarnings("unchecked")
	public void readFrom(DataInput in) throws IOException {
		messageId = in.readLong();
		if (in.readBoolean()) {
			Class<? extends ISdfsSerializable> clazz = SdfsSerializationRegistry.getClazz(in.readInt());
			try {
				message = (T) clazz.newInstance();
			} catch (Exception e) {
				throw new IOException(e);
			}
			message.readFrom(in);
		}
		if (in.readBoolean()) {
			exception = new RpcException();
			exception.readFrom(in);
		}
	}

	public void writeTo(DataOutput out) throws IOException {
		out.writeLong(messageId);
		out.writeBoolean(message != null);
		if (message != null) {
			out.writeInt(SdfsSerializationRegistry.getType(message.getClass()));
			message.writeTo(out);
		}
		out.writeBoolean(exception != null);
		if (exception != null) {
			exception.writeTo(out);
		}
	}

	@Override
	public String toString() {
		return "RpcMessage [messageId=" + messageId + ", message=" + message
				+ ", exception=" + exception + "]";
	}
}
