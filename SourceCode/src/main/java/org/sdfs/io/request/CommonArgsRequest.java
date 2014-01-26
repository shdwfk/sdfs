package org.sdfs.io.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.DataCell;

public class CommonArgsRequest implements IRequest {
	private String methodName;
	private DataCell[] args;

	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public DataCell[] getArgs() {
		return args;
	}
	public void setArgs(DataCell[] args) {
		this.args = args;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		methodName = in.readUTF();
		int argsCount = in.readInt();
		args = new DataCell[argsCount];
		for (int i = 0; i < argsCount; ++ i) {
			args[i] = new DataCell();
			args[i].readFrom(in);
		}
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeUTF(methodName);
		out.writeInt(args.length);
		for (DataCell dataCell : args) {
			dataCell.writeTo(out);
		}
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.COMMON_ARGS_REQUEST;
	}
}
