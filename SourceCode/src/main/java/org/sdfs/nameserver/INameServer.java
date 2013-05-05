package org.sdfs.nameserver;

import java.util.Iterator;

import org.sdfs.commons.FileObjectDescriptor;
import org.sdfs.exceptions.SdfsException;

/**
 * NameServer的抽象接口。<br/>
 * 管理文件路径（String类型）与FileObjectDescriptor之间的映射关系，
 * 以及文件路径的目录支持。
 * @author wangfk
 *
 */
public interface INameServer {
	/** 通过文件路径（filePath）获取FileObjectDescriptor */
	public FileObjectDescriptor fileIdByFilePath(String filePath)
			throws SdfsException;

	/** 增加一条新的记录：filePath-->FileObjectDescriptor */
	public void addNewFile(String filePath,
			FileObjectDescriptor fileObjectDescriptor) throws SdfsException;

	/** 更新一条记录：filePath-->FileObjectDescriptor */
	public void updateFile(String filePath,
			FileObjectDescriptor fileObjectDescriptor) throws SdfsException;

	/** 移除一条记录，将要移除filePath的FileObjectDescriptor返回 */
	public FileObjectDescriptor removeFileId(String filePath)
			throws SdfsException;

	/** 对文件路径的目录支持 */
	public Iterator<String> listSubFilePath(String filePath, String depth)
			throws SdfsException;

	/** 对文件路径的遍历支持 */
	public Iterator<String> seekAndIterate(String filePath) throws SdfsException;
}
