package org.sdfs.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 序列化接口，声明了序列化和反序列化方法
 * @author wangfk
 *
 */
public interface ISdfsSerializable {
	/**
	 * 反序列化接口方法
	 * @param in
	 * @throws IOException
	 */
	public void readFrom(DataInput in) throws IOException;
	/**
	 * 序列化接口方法
	 * @param out
	 * @throws IOException
	 */
	public void writeTo(DataOutput out) throws IOException;
}
