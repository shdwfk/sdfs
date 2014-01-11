package org.sdfs.nameserver;

import java.util.Iterator;

import org.sdfs.commons.FileObjectDescriptor;
import org.sdfs.exceptions.SdfsException;

/**
 * INameServer的实现类
 * @author wangfk
 *
 */
public class NameServerImpl implements INameServer {

	@Override
	public FileObjectDescriptor fileIdByFilePath(String filePath)
			throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNewFile(String filePath,
			FileObjectDescriptor fileObjectDescriptor) throws SdfsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFile(String filePath,
			FileObjectDescriptor fileObjectDescriptor) throws SdfsException {
		// TODO Auto-generated method stub

	}

	@Override
	public FileObjectDescriptor removeFileId(String filePath)
			throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> listSubFilePath(String filePath, String depth)
			throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> seekAndIterate(String filePath)
			throws SdfsException {
		// TODO Auto-generated method stub
		return null;
	}

}
