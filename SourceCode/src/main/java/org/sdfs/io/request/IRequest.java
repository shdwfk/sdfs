package org.sdfs.io.request;

import org.sdfs.io.ISdfsSerializable;

/**
 * Request接口，声明了 {@link #getRequestType()}方法，以获取request类型
 * @author wangfk
 *
 */
public interface IRequest extends ISdfsSerializable {
	public RequestType getRequestType();
}
