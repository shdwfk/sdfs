package org.sdfs.io.response;

import org.sdfs.io.ISdfsSerializable;

/**
 * Response接口，声明了 {@link #getResponseType()}方法，以获取response类型
 * @author wangfk
 *
 */
public interface IResponse extends ISdfsSerializable {
	public ResponseType getResponseType();
}
