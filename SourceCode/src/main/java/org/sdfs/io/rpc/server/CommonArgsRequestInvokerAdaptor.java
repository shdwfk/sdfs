package org.sdfs.io.rpc.server;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;

public class CommonArgsRequestInvokerAdaptor<T> implements IRequestInvokeAdaptor {
	private T rpcService;

	public CommonArgsRequestInvokerAdaptor(T rpcService) {
		this.rpcService = rpcService;
	}


	@Override
	public IResponse invokeRequest(IRequest request) throws Exception {
		if (request.getRequestType() != RequestType.COMMON_ARGS_REQUEST) {
			throw new RpcException("Unsupported request type");
		}

		
		//TODO 通过反射机制来做调用
		
		return null;
	}

}
