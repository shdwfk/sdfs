package org.sdfs.superblock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.sdfs.commons.FileMeta;
import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;

/**
 * Mock object for ISuperBlock
 * 
 * @author wangfk
 *
 */
public class MockSuperBlock implements ISuperBlock {
	final long blockId;
	long version = 0;
	Map<Long, byte[]> fileData = Collections.synchronizedMap(new HashMap<Long, byte[]>());
	Map<Long, FileMeta> fileMetas = Collections.synchronizedMap(new HashMap<Long, FileMeta>());
	Set<Long> removeFlags = Collections.synchronizedSet(new HashSet<Long>());

	public MockSuperBlock() {
		this.blockId = 1L;
	}

	public MockSuperBlock(long blockId) {
		this.blockId = blockId;
	}

	public IFileObject getFileObject(long key) throws SdfsException {
		return new MockFileObject(key);
	}

	public long getFileCount() {
		long result = 0;
		for (Long key : fileData.keySet()) {
			if (!removeFlags.contains(key)) {
				++ result;
			}
		}
		return result;
	}

	public long getAvailableSize() {
		long size = 0L;
		for (Entry<Long, byte[]> data : fileData.entrySet()) {
			if (!removeFlags.contains(data.getKey())) {
				size += data.getValue().length;
			}
		}
		return size;
	}

	public long getTotalSize() {
		long size = 0;
		for (byte[] data : fileData.values()) {
			size += data.length;
		}
		return size;
	}

	public void compact() throws SdfsException {
		for (long fileId : removeFlags) {
			fileData.remove(fileId);
			fileMetas.remove(fileId);
		}
		removeFlags.clear();
	}

	public SuperBlockInfo getBlockInfo() {
		SuperBlockInfo superBlockInfo = new SuperBlockInfo();
		superBlockInfo.setBlockId(blockId);
		superBlockInfo.setFileCount(getFileCount());
		superBlockInfo.setAvailableSize(getAvailableSize());
		superBlockInfo.setTotalSize(getTotalSize());
		
		return superBlockInfo;
	}

	public long generateFileKey(long version) throws SdfsException {
		if (version != this.version + 1) {
			throw new SdfsException(
					"Invalid version, block current version is " + this.version
					+ ", and the user version is " + version);
		}
		return this.version + 1;
	}

	class MockFileObject implements IFileObject {
		private long fileId;

		public MockFileObject(long fileId) {
			this.fileId = fileId;
		}

		public InputStream openFile() throws SdfsException {
			return new ByteArrayInputStream(getFileData());
		}

		public InputStream openFile(long offset, long size)
				throws SdfsException {
			return new ByteArrayInputStream(getFileData(), (int)offset, (int)size);
		}

		public OutputStream createFile(final FileMeta fileMeta) throws SdfsException {
			if (fileExists()) {
				throw new SdfsException("The file does exist, BlockId="
						+ blockId + ", fileId=" + fileId);
			}

			return new ByteArrayOutputStream(){
				public void close() throws IOException {
					if (version + 1 != fileId) {
						throw new IOException(
								new SdfsException(
										"Invalid fileId, fileId=" + fileId +
										", and block version=" + version));
					}
					fileData.put(fileId, this.toByteArray());
					fileMetas.put(fileId, fileMeta);
					removeFlags.remove(fileId);
					++ version;
				}
			};
		}

		public OutputStream appendFile() throws SdfsException {
			OutputStream outputStream = createFile(getFileMeta()); 
			try {
				IOUtils.write(getFileData(), outputStream);
			} catch (IOException e) {
				throw new SdfsException(e);
			}
			return outputStream;
		}

		public FileMeta getFileMeta() throws SdfsException {
			checkFileExists();
			return fileMetas.get(fileId);
		}

		public void updateFileMeta(FileMeta fileMeta) throws SdfsException {
			checkFileExists();
			fileMetas.put(fileId, fileMeta);
		}

		public void delete() throws SdfsException {
			if (!fileData.containsKey(fileId)) {
				throw new SdfsException("The file does not exist, BlockId="
						+ blockId + ", fileId=" + fileId);
			}
			if (removeFlags.contains(fileId)) {
				throw new SdfsException("The file was deleted already, BlockId="
						+ blockId + ", fileId=" + fileId);
			}
			removeFlags.add(fileId);
		}

		public long getFileSize() {
			try {
				return getFileData().length;
			} catch (SdfsException e) {
			}
			return 0;
		}

		public boolean fileExists() {
			return fileData.containsKey(fileId) && !removeFlags.contains(fileId);
		}

		private byte[] getFileData() throws SdfsException {
			checkFileExists();
			return fileData.get(fileId);
		}

		private void checkFileExists() throws SdfsException {
			if (!fileExists()) {
				throw new SdfsException("The file does not exist, BlockId="
						+ blockId + ", fileId=" + fileId);
			}
		}
	}
}
