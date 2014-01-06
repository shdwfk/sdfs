package org.sdfs.io.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.response.IResponse;
import org.sdfs.io.response.ResponseType;

public class CalculateResponse implements IResponse {
	private int result;

	public CalculateResponse(int result) {
		this.result = result;
	}

	public CalculateResponse() {
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		result = in.readInt();
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeInt(result);
	}

	@Override
	public ResponseType getResponseType() {
		return ResponseType.TEST_CASE_TYPE;
	}

	public int getResult() {
		return result;
	}
}
