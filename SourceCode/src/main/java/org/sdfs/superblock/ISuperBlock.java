package org.sdfs.superblock;

import org.sdfs.exceptions.SdfsException;


/**
 * SuperBlock的抽象接口
 * @author wangfk
 *
 */
public interface ISuperBlock {

	/** 获取一个file object */
	public IFileObject getFileObject(long key) throws SdfsException;

	/** 得到SuperBlock中文件总数 */
	public long getFileCount();

	/** 得到有效的数据大小 */
	public long getAvailableSize();

	/** 得到总体数据大小 */
	public long getTotalSize();

	/** 在经历了多次写入、删除之后，需要对整个文件做compact，压缩空间占用 */
	public void compact() throws SdfsException;
}
