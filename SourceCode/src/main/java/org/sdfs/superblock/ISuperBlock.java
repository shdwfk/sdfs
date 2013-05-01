package org.sdfs.superblock;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * SuperBlock的抽象接口
 * @author wangfk
 *
 */
public interface ISuperBlock {
	/** 读取文件流 */
	public InputStream readFile(FileKey key);

	/** 读取文件流的片段 */
	public  InputStream readFile(FileKey key, long offset, long size);

	/** 创建一个文件，打开文件写入流 */
	public OutputStream writeFile(FileKey key, FileMeta fileMeta);

	/** 打开一个文件，用来追加 */
	public OutputStream appendFile(FileKey key);

	/** 获取文件的meta信息 */
	public FileMeta getFileMeta(FileKey key);

	/** 更改一个文件的FileMeta */
	public void updateFileMeta(FileKey key, FileMeta fileMeta);

	/** 得到文件的大小 */
	public long getFileSize(FileKey key);

	/** 得到SuperBlock中文件总数 */
	public long getFileCount();

	/** 查看一个文件是不是存在 */
	public boolean fileExists(FileKey key);

	/** 得到有效的数据大小 */
	public long getAvailableSize();

	/** 得到总体数据大小 */
	public long getTotalSize();

	/** 在经历了多次写入、删除之后，需要对整个文件做compact，压缩空间占用 */
	public void compact();
}

interface FileKey {};