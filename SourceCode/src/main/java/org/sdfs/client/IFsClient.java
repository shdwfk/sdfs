package org.sdfs.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.sdfs.commons.FileMeta;
import org.sdfs.exceptions.SdfsClientException;

/**
 * SDFS client的抽象接口
 * @author wangfk
 *
 */
public interface IFsClient {
	/** 打开一个文件，返回输入流 */
	public InputStream openFile(String filePath) throws SdfsClientException;

	/** 打开一个文件片段，返回输入流 */
	public InputStream openFile(String filePath, long position, long size)
			throws SdfsClientException;

	/** 创建一个文件，返回输出流 */
	public OutputStream createFile(String filePath, FileMeta fileMeta)
			throws SdfsClientException;

	/** 追加的方式打开一个已存在文件，返回输出流 */
	public OutputStream appendFile(String filePath) throws SdfsClientException;

	/** 删除一个文件 */
	public void removeFile(String filePath) throws SdfsClientException;

	/** 获取一个文件的元数据信息 */
	public FileMeta getFileMeta(String filePath) throws SdfsClientException;

	/** 更新一个文件的元数据信息 */
	public FileMeta updateFileMeta(String filePath) throws SdfsClientException;

	/** 查看文件是否存在 */
	public boolean fileExists(String filePath) throws SdfsClientException;

	/** 对文件路径的目录支持 */
	public Iterator<String> listSubFilePath(String filePath, String depth)
			throws SdfsClientException;

	/** 对文件路径的遍历支持 */
	public Iterator<String> seekAndIterate(String filePath)
			throws SdfsClientException;
}
