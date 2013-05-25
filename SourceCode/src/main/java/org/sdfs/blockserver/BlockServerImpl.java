package org.sdfs.blockserver;

import java.util.List;

import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.superblock.ISuperBlock;

/**
 * IBlockServer的实现类
 * @author wangfk
 *
 */
public class BlockServerImpl implements IBlockServer {

	public ISuperBlock getSuperBlock(long blockId) throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SuperBlockInfo> getAllSuperBlockInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSuperBlock(long blockId) throws SdfsException {
		// TODO Auto-generated method stub
	}

	public void addNewSuperBlock(long blockId) throws SdfsException {
		// TODO Auto-generated method stub

	}

	public long getAvailableSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void execCommand(ISdfsCommand sdfsCommand) throws SdfsException {
		// TODO Auto-generated method stub
	}

}
