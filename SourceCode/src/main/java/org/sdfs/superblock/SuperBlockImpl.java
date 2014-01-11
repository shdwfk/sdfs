package org.sdfs.superblock;

import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;

/**
 * ISuperBlock的实现类
 * @author wangfk
 *
 */
public class SuperBlockImpl implements ISuperBlock {

	@Override
	public IFileObject getFileObject(long key) throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getFileCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAvailableSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void compact() throws SdfsException {
		// TODO Auto-generated method stub

	}

	@Override
	public SuperBlockInfo getBlockInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getVersion() throws SdfsException {
		// TODO Auto-generated method stub
		return 0;
	}
}
