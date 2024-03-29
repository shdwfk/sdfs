package org.sdfs.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Ignore;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;

@Ignore
public class FetchFileRequest implements IRequest {
	static {
		SdfsSerializationRegistry.register(-2, FetchFileRequest.class);
	}

	/** FileObject所在的SuperBlock的blockId */
	private long blockId;
	/** FileObject的fileKey */
	private long fileKey;

	public long getBlockId() {
		return blockId;
	}

	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}

	public long getFileKey() {
		return fileKey;
	}

	public void setFileKey(long fileKey) {
		this.fileKey = fileKey;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		blockId = in.readLong();
		fileKey = in.readLong();
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeLong(blockId);
		out.writeLong(fileKey);
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.FETCH_FILE;
	}

}
