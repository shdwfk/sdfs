package org.sdfs.io.response;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.DataCell;
import org.sdfs.io.rpc.RpcException;

public class CommonArgsResponse implements IResponse {
	private DataCell result;
	private RpcException exception;

	public boolean isExceptional() {
		return exception == null;
	}

	public DataCell getResult() {
		return result;
	}

	public void setResult(DataCell result) {
		this.result = result;
	}

	public RpcException getException() {
		return exception;
	}

	public void setException(RpcException exception) {
		this.exception = exception;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return null;
	}

}
