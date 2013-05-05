package org.sdfs.blockserver;

import java.util.List;

import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.superblock.ISuperBlock;

/**
 * BlockServer的抽象接口
 * @author wangfk
 *
 */
public interface IBlockServer {

	/** 通过blockId得到一个SuperBlock对象  */
	public ISuperBlock getSuperBlock(long blockId) throws SdfsException;

	/** 得到BlockServer下的所有SuperBlock对象的SuperBlockInfo信息 */
	public List<SuperBlockInfo> getAllSuperBlockInfos();

	/** 删除一个SuperBlock对象 */
	public void removeSuperBlock(long blockId) throws SdfsException;

	/** 新建一个SuperBlock对象 */
	public void addNewSuperBlock(long blockId) throws SdfsException;

	/** 得到本BlockServer负责的有效的数据大小 */
	public long getAvailableSize();

	/** 得到本BlockServer负责的总体数据大小 */
	public long getTotalSize();

	/** 执行BlockServer给的命令 */
	public void execCommand(ISdfsCommand sdfsCommand) throws SdfsException;
}
