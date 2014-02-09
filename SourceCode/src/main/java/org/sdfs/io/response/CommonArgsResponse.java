package org.sdfs.io.response;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.DataCell;

public class CommonArgsResponse implements IResponse {
	private DataCell result;

	public DataCell getResult() {
		return result;
	}

	public void setResult(DataCell result) {
		this.result = result;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		if (in.readBoolean()) {
			result = new DataCell();
			result.readFrom(in);
		} else {
			result = null;
		}
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeBoolean(result != null);
		if (result != null) {
			result.writeTo(out);
		}
	}

	@Override
	public ResponseType getResponseType() {
		return ResponseType.COMMON_ARGS_RESPONSE;
	}

}
