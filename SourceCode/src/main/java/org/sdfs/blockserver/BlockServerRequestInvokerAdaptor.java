package org.sdfs.blockserver;

import java.io.IOException;
import java.io.OutputStream;

import org.sdfs.exceptions.SdfsException;
import org.sdfs.io.request.AddNewFileRequest;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.AddNewFileResponse;
import org.sdfs.io.response.IResponse;
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
		default:
			break;
		}
		return null;
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
}
