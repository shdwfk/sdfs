package org.sdfs.io.rpc.server.interfaces;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;

/**
 * request执行逻辑的适配器，由{@link IRequestExecutor}来调用
 * @author wangfk
 *
 */
public interface IRequestInvokeAdaptor {
	public IResponse invokeRequest(IRequest request) throws Exception;
}
