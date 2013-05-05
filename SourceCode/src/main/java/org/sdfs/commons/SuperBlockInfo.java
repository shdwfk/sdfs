package org.sdfs.commons;

public class SuperBlockInfo {
	private long blockId;
	private long totalSize;
	private long availableSize;
	private long fileCount;

	public long getBlockId() {
		return blockId;
	}
	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getAvailableSize() {
		return availableSize;
	}
	public void setAvailableSize(long availableSize) {
		this.availableSize = availableSize;
	}
	public long getFileCount() {
		return fileCount;
	}
	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}
}
