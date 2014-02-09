package org.sdfs.io.rpc.server;


public class CommonArgsRequestRpcServer<T> extends RequestRpcServer {

	public CommonArgsRequestRpcServer(int requestExecuteConcurrency,
			int messageHandleConcurrency, T service) {
		super(requestExecuteConcurrency, messageHandleConcurrency,
				new CommonArgsRequestInvokerAdaptor<>(service));
	}
}
