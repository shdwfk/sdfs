package org.sdfs.io.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sdfs.io.ISdfsSerializable;
import org.sdfs.io.SdfsSerializationHelper;

/**
 * Rpc调用时的exception，支持序列化，能在服务器端和客户端之间传递
 * @author wangfk
 *
 */
public final class RpcException extends Exception implements ISdfsSerializable {

	private static final long serialVersionUID = -6636213114847941211L;

	private String causeClassName;
	private String message;

	private boolean constractedByDeserialization = false;

	public RpcException() {
		super();
		init(null, null);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
		init(message, cause);
	}

	public RpcException(String message) {
		super(message);
		init(message, null);
	}

	public RpcException(Throwable cause) {
		super(cause);
		init(cause.getMessage(), cause);
	}


	private void init(String message, Throwable cause) {
		if (cause != null) {
			this.causeClassName = cause.getClass().getName();
		} else {
			this.causeClassName = "<null>";
		}
		this.message = message;
	}

	@Override
	public String getMessage() {
		if(constractedByDeserialization) {
			return message;
		} else {
			return super.getMessage();
		}
	}

	public String getCauseClassName() {
		return causeClassName;
	}

	public void readFrom(DataInput in) throws IOException {
		causeClassName = in.readUTF();
		message = SdfsSerializationHelper.readStringNull(in);

		List<StackTraceElement> allStackTrace = new ArrayList<StackTraceElement>();

		StackTraceElement[] remoteStackTrace = readStackTrace(in);
		allStackTrace.addAll(Arrays.asList(remoteStackTrace));

		if (in.readBoolean()) {
			allStackTrace.add(new StackTraceElement(
					"...", "nested exception type: " + SdfsSerializationHelper.readStringNull(in), "...", -1));
			allStackTrace.add(new StackTraceElement(
					"...", "nested exception message: " + SdfsSerializationHelper.readStringNull(in), "...", -1));
			allStackTrace.add(new StackTraceElement(
					"...", "--- nested exception stacktrace ---", "...", -1));
			StackTraceElement[] nestedStackTrace = readStackTrace(in);
			allStackTrace.addAll(Arrays.asList(nestedStackTrace));
		}

		allStackTrace.add(new StackTraceElement(
				"...", "--- sdfs rpc call ---", "...", -1));
		allStackTrace.addAll(Arrays.asList(new Exception().getStackTrace()));

		setStackTrace(allStackTrace.toArray(new StackTraceElement[0]));

		constractedByDeserialization = true;
	}

	public void writeTo(DataOutput out) throws IOException {
		out.writeUTF(causeClassName);
		SdfsSerializationHelper.writeStringNull(out, message);

		writeStackTrace(out, super.getStackTrace());

		Throwable cause = getCause();
		out.writeBoolean(cause != null);
		if (cause != null) {
			SdfsSerializationHelper.writeStringNull(out, cause.getClass().getName());
			SdfsSerializationHelper.writeStringNull(out, cause.getMessage());
			writeStackTrace(out, cause.getStackTrace());
		}

	}

	private void writeStackTrace(DataOutput out, StackTraceElement[] stackTrace) throws IOException {
		out.writeInt(stackTrace.length);
		for (StackTraceElement e : stackTrace) {
			SdfsSerializationHelper.writeStringNull(out, e.getClassName());
			SdfsSerializationHelper.writeStringNull(out, e.getMethodName());
			SdfsSerializationHelper.writeStringNull(out, e.getFileName());
			out.writeInt(e.getLineNumber());
		}
	}

	private StackTraceElement[] readStackTrace(DataInput in) throws IOException {
		int size = in.readInt();
		StackTraceElement[] stackTrace = new StackTraceElement[size];
		for (int i = 0; i < size; ++ i) {
			stackTrace[i] = new StackTraceElement(
					SdfsSerializationHelper.readStringNull(in),
					SdfsSerializationHelper.readStringNull(in),
					SdfsSerializationHelper.readStringNull(in),
					in.readInt());
		}
		return stackTrace;
	}
}
