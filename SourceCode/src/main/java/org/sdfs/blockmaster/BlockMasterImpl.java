package org.sdfs.blockmaster;

import java.net.InetSocketAddress;
import java.util.List;

import org.sdfs.commons.FileObjectDescriptor;
import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;

/**
 * IBlockMaster的实现类
 * @author wangfk
 *
 */
public class BlockMasterImpl implements IBlockMaster {

	@Override
	public List<InetSocketAddress> getSuperBlockLocations(long blockId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InetSocketAddress> getAllBlockServers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InetSocketAddress> reportBlockServerStatus(
			InetSocketAddress blockServer, List<SuperBlockInfo> superBlockList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISdfsCommand> generateCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileObjectDescriptor createNewFileObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
