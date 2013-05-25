package org.sdfs.blockmaster;

import java.net.InetSocketAddress;
import java.util.List;

import org.sdfs.commons.FileObjectDescriptor;
import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;

/**
 * BlockMaster的抽象接口
 * @author wangfk
 *
 */
public interface IBlockMaster {

	/** 对于特定的一个SuperBlock，给出这个block所在的BlockServer的列表 */
	public List<InetSocketAddress> getSuperBlockLocations(long blockId);

	/** 得到目前所有的BlockServer的列表 */
	public List<InetSocketAddress> getAllBlockServers();

	/** BlockServer向BlockMaster汇报其负责的所有的SuperBlock的状态列表 */
	public List<InetSocketAddress> reportBlockServerStatus(
			InetSocketAddress blockServer, List<SuperBlockInfo> superBlockList);

	/** 生成调度命令，供BlockServer来执行 */
	public List<ISdfsCommand> generateCommands();

	/** 拟创建一个新FileObject，返回该FileObject的文件描述符 */
	public FileObjectDescriptor createNewFileObject();
}
