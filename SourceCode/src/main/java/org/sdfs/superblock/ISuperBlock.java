package org.sdfs.superblock;

import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;


/**
 * SuperBlock的抽象接口
 * @author wangfk
 *
 */
public interface ISuperBlock {

	public void init(String blockDir, long blockId) throws SdfsException;

	/** 获取一个FileObject */
	public IFileObject getFileObject(long key) throws SdfsException;

	/** 得到SuperBlock中文件总数 */
	public long getFileCount();

	/** 得到有效的数据大小 */
	public long getAvailableSize();

	/** 得到总体数据大小 */
	public long getTotalSize();

	/** 在经历了多次写入、删除之后，需要对整个文件做compact，压缩空间占用 */
	public void compact() throws SdfsException;

	/** 获取本SuperBlock的SuperBlockInfo */
	public SuperBlockInfo getBlockInfo();

	/** 生成一个新的file key，该key与已存在的key不重复 */
	public long getVersion() throws SdfsException;
}
