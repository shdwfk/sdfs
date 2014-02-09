package org.sdfs.io.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Future;

import org.sdfs.io.DataCell;
import org.sdfs.io.request.CommonArgsRequest;
import org.sdfs.io.response.CommonArgsResponse;
import org.sdfs.io.response.IResponse;

public class CommonArgsRequestRpcClient {

	private static class RpcInvocationHandler implements InvocationHandler {
		private RequestRpcClient requestRpcClient;

		public RpcInvocationHandler(RequestRpcClient requestRpcClient) {
			super();
			this.requestRpcClient = requestRpcClient;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			// TODO Auto-generated method stub
			CommonArgsRequest commonArgsRequest = new CommonArgsRequest();
			commonArgsRequest.setMethodName(method.getName());
			Class<?>[] parameterTypes = method.getParameterTypes();
			DataCell[] argsData = new DataCell[args.length];
			for (int i = 0; i < argsData.length; ++ i) {
				argsData[i] = DataCell.fromDataObject(parameterTypes[i], args[i]);
			}
			commonArgsRequest.setArgs(argsData);
			Future<IResponse> rpcCall = requestRpcClient.rpcCall(commonArgsRequest);
			CommonArgsResponse response = (CommonArgsResponse) rpcCall.get();
			DataCell result = response.getResult();
			return result.getDataObject();
		}
		
	}

	public static <T> T getRpcCallStub(Class<T> clazz, String host, int port,
			long callTimeOutMillis, int maxRequestBufferSize,
			long cleanCallFutureIntervalMills) {
		RequestRpcClient requestRpcClient = new RequestRpcClient()
				.setCallTimeOutMillis(callTimeOutMillis)
				.setCleanCallFutureIntervalMills(cleanCallFutureIntervalMills)
				.setMaxRequestBufferSize(maxRequestBufferSize);
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class<?>[]{clazz}, new RpcInvocationHandler(requestRpcClient));
	}
}
