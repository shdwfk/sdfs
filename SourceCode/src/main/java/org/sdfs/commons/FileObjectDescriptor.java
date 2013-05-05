package org.sdfs.commons;

/**
 * FileObject的描述符，由两个部分组成：blockId（long类型）和fileKey（long类型）
 * @author wangfk
 *
 */
public class FileObjectDescriptor {
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
}
