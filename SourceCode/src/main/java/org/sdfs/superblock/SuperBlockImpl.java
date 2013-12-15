package org.sdfs.superblock;

import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;

/**
 * ISuperBlock的实现类
 * @author wangfk
 *
 */
public class SuperBlockImpl implements ISuperBlock {

	public IFileObject getFileObject(long key) throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getFileCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getAvailableSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void compact() throws SdfsException {
		// TODO Auto-generated method stub

	}

	public SuperBlockInfo getBlockInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public long generateFileKey(long version) throws SdfsException {
		// TODO Auto-generated method stub
		return 0;
	}
}
