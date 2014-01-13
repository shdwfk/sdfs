package org.sdfs.blockserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.io.request.AddNewFileRequest;
import org.sdfs.io.request.FetchFileRequest;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.AddNewFileResponse;
import org.sdfs.io.response.FetchFileResponse;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;
import org.sdfs.superblock.IFileObject;
import org.sdfs.superblock.ISuperBlock;

public class BlockServerRequestInvokerAdaptor implements IRequestInvokeAdaptor {
	private IBlockServer blockServer;

	public BlockServerRequestInvokerAdaptor(IBlockServer blockServer) {
		this.blockServer = blockServer;
	}

	@Override
	public IResponse invokeRequest(IRequest request) throws Exception {
		switch (request.getRequestType()) {
		case ADD_NEW_FILE:
			return addNewFile((AddNewFileRequest) request);
		case FETCH_FILE:
			return fetchFile((FetchFileRequest) request);
		default:
			break;
		}
		throw new RpcException("Unkown request type: " + request.getRequestType());
	}

	private AddNewFileResponse addNewFile(AddNewFileRequest request
			) throws SdfsException, IOException {
		ISuperBlock superBlock = blockServer.getSuperBlock(request.getBlockId());
		IFileObject fileObject = superBlock.getFileObject(request.getFileKey());
		OutputStream outputStream = fileObject.createFile(null);
		try {
			outputStream.write(request.getFileData());
		} finally {
			outputStream.close();
		}
		AddNewFileResponse addNewFileResponse = new AddNewFileResponse();
		addNewFileResponse.setSuccessful(true);
		return addNewFileResponse;
	}

	private FetchFileResponse fetchFile(FetchFileRequest request)
			throws SdfsException, IOException {
		ISuperBlock superBlock = blockServer.getSuperBlock(request.getBlockId());
		IFileObject fileObject = superBlock.getFileObject(request.getFileKey());
		if (!fileObject.fileExists()) {
			throw new SdfsException("The file does not exist, blockId="
					+ request.getBlockId() + ", fileKey=" + request.getFileKey());
		}
		InputStream is = fileObject.openFile();
		try {
			byte[] fileData = IOUtils.toByteArray(is);
			FetchFileResponse fetchFileResponse = new FetchFileResponse();
			fetchFileResponse.setFileData(fileData);
			return fetchFileResponse;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
