package org.sdfs.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Ignore;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.response.ResponseType;

@Ignore
public class FetchFileResponse implements IResponse {
	static {
		SdfsSerializationRegistry.register(-4, FetchFileResponse.class);
	}

	private byte[] fileData;

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public long fileSize() {
		return fileData.length;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		int fileSize = in.readInt();
		fileData = new byte[fileSize];
		in.readFully(fileData);
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeInt(fileData.length);
		out.write(fileData);
	}

	@Override
	public ResponseType getResponseType() {
		return ResponseType.FETCH_FILE;
	}
}
