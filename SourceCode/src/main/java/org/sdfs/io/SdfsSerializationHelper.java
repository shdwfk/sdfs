package org.sdfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * 协助对象序列化、反序列化的构造
 * 
 * @author wangfk
 *
 */
public class SdfsSerializationHelper {
	/**
	 * 反序列化对象：|对象类型(int)|对象序列化数据|
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ISdfsSerializable> T readObjectWithoutLen(InputStream in) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(in);
		int type = dataInputStream.readInt();
		Class<? extends ISdfsSerializable> clazz = SdfsSerializationRegistry.getClazz(type);
		T instance = null;
		try {
			instance = (T) clazz.newInstance();
		} catch (Exception e) {
			throw new IOException(e);
		}
		instance.readFrom(dataInputStream);
		return instance;
	}

	/**
	 * 序列化对象：|对象类型(int)|对象序列化数据|
	 * @param out
	 * @throws IOException
	 */
	public static <T extends ISdfsSerializable> void writeObjectWithoutLen(OutputStream out, T object) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(out);
		int type = SdfsSerializationRegistry.getType(object.getClass());
		dataOutputStream.writeInt(type);
		object.writeTo(dataOutputStream);
	}

	/**
	 * 反序列化对象：|对象类型(int)|对象序列化数据|
	 * @param data
	 * @return
	 * @throws IOException
	 * @see {@link #readObjectWithoutLen(InputStream)}
	 */
	public static <T extends ISdfsSerializable> T readObjectWithoutLen(byte[] data) throws IOException {
		return readObjectWithoutLen(new ByteArrayInputStream(data));
	}

	/**
	 * 序列化对象：|对象类型(int)|对象序列化数据|
	 * @param object
	 * @return
	 * @throws IOException
	 * @see {@link #writeObjectWithoutLen(OutputStream, ISdfsSerializable)}
	 */
	public static <T extends ISdfsSerializable> byte[] writeObjectWithoutLen(T object) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			writeObjectWithoutLen(byteArrayOutputStream, object);
			return byteArrayOutputStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(byteArrayOutputStream);
		}
	}

	/**
	 * 反序列化对象：|对象长度(int)|对象类型(int)|对象序列化数据|
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static <T extends ISdfsSerializable> T readObject(InputStream in) throws IOException {
		in.skip(4);
		return readObjectWithoutLen(in);
	}

	/**
	 * 序列化对象：|对象长度(int)|对象类型(int)|对象序列化数据|
	 * @param out
	 * @param object
	 * @throws IOException
	 */
	public static <T extends ISdfsSerializable> void writeObject(OutputStream out, T object) throws IOException {
		byte[] data = writeObjectWithoutLen(object);
		int len = data.length;
		out.write((len >>> 24) & 0xFF);
        out.write((len >>> 16) & 0xFF);
        out.write((len >>>  8) & 0xFF);
        out.write((len >>>  0) & 0xFF);
        out.write(data);
	}


	/**
	 * 反序列化对象：|对象长度(int)|对象类型(int)|对象序列化数据|
	 * @param in
	 * @return
	 * @throws IOException
	 * @see {@link #readObject(InputStream)}
	 */
	public static <T extends ISdfsSerializable> T readObject(byte[] data) throws IOException {
		return readObject(new ByteArrayInputStream(data));
	}

	/**
	 * 序列化对象：|对象长度(int)|对象类型(int)|对象序列化数据|
	 * @param out
	 * @param object
	 * @throws IOException
	 * @see {@link #writeObject(OutputStream, ISdfsSerializable)}
	 */
	public static <T extends ISdfsSerializable> byte[] writeObject(T object) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeObject(out, object);
		return out.toByteArray();
	}
}
