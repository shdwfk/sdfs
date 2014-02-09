package org.sdfs.io.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.sdfs.io.DataCell;
import org.sdfs.io.request.CommonArgsRequest;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;
import org.sdfs.io.response.CommonArgsResponse;
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
		CommonArgsRequest commonArgsRequest = (CommonArgsRequest) request;
		DataCell[] args = commonArgsRequest.getArgs();
		Object[] argDatas = new Object[args == null ? 0 : args.length];
		if (argDatas != null) {
			for (int i = 0; i < args.length; ++ i) {
				argDatas[i] = args[i].getDataObject();
			}
		}

		Method method = getInvokeMethod(commonArgsRequest);
		Object returnValue = method.invoke(rpcService, argDatas);
		DataCell dataCell =
				DataCell.fromDataObject(method.getReturnType(), returnValue);
		CommonArgsResponse response = new CommonArgsResponse();
		response.setResult(dataCell);
		return response;
	}

	private Map<String, Method> methodCache = new HashMap<>();
	private Method getInvokeMethod(CommonArgsRequest commonArgsRequest)
			throws NoSuchMethodException, SecurityException {
		String methodDescription = commonArgsRequest.getMethodDescription();
		if (methodCache.containsKey(methodDescription)) {
			return methodCache.get(methodDescription);
		} else {
			DataCell[] args = commonArgsRequest.getArgs();
			Class<?>[] argTypes = new Class<?>[args == null ? 0 : args.length];
			if (args != null) {
				for (int i = 0; i < args.length; ++ i) {
					argTypes[i] = args[i].getDataClassType();
				}
			}
			Method method = rpcService.getClass().getMethod(
					commonArgsRequest.getMethodName(), argTypes);
			methodCache.put(methodDescription, method);
			return method;
		}
	}
}
