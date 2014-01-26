package org.sdfs.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Ignore;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;

/**
 * 用来向BlockServer请求，添加新的文件
 * @author wangfk
 *
 */
@Ignore
public class AddNewFileRequest implements IRequest {
	static {
		SdfsSerializationRegistry.register(-1, AddNewFileRequest.class);
	}
	/** FileObject所在的SuperBlock的blockId */
	private long blockId;
	/** FileObject的fileKey */
	private long fileKey;
	/** FileObject的数据 */
	private byte[] fileData;

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
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public void readFrom(DataInput in) throws IOException {
		blockId = in.readLong();
		fileKey = in.readLong();
		int fileLen = in.readInt();
		fileData = new byte[fileLen];
		in.readFully(fileData);
	}
	public void writeTo(DataOutput out) throws IOException {
		out.writeLong(blockId);
		out.writeLong(fileKey);
		out.writeInt(fileData.length);
		out.write(fileData);
	}
	public RequestType getRequestType() {
		return RequestType.ADD_NEW_FILE;
	}

	@Override
	public String toString() {
		return "AddNewFileRequest [blockId=" + blockId + ", fileKey=" + fileKey
				+ ", fileData length=" + fileData.length + "]";
	}
}
