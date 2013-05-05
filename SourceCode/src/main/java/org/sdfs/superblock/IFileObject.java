package org.sdfs.superblock;

import java.io.InputStream;
import java.io.OutputStream;

import org.sdfs.commons.FileMeta;
import org.sdfs.exceptions.SdfsException;

public interface IFileObject {
	/** 读取文件流 */
	public InputStream openFile() throws SdfsException;

	/** 读取文件流的片段 */
	public InputStream openFile(long offset, long size) throws SdfsException;

	/** 创建一个文件，打开文件写入流 */
	public OutputStream createFile(FileMeta fileMeta) throws SdfsException;

	/** 打开一个文件，用来追加 */
	public OutputStream appendFile() throws SdfsException;

	/** 获取文件的meta信息 */
	public FileMeta getFileMeta() throws SdfsException;

	/** 更改一个文件的FileMeta */
	public void updateFileMeta(FileMeta fileMeta) throws SdfsException;

	/** 删除一个文件 */
	public void delete() throws SdfsException;

	/** 得到文件的大小 */
	public long getFileSize();

	/** 查看一个文件是不是存在 */
	public boolean fileExists();
}
