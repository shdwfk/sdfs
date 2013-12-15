package org.sdfs.io;

import java.util.HashMap;

import org.sdfs.io.request.AddNewFileRequest;
import org.sdfs.io.response.AddNewFileResponse;

/**
 * 对象注册，每一个可序列化对象都需要在这里注册
 * @author wangfk
 *
 */
public class SdfsSerializationRegistry {
	static final HashMap<Integer, Class<? extends ISdfsSerializable>> clazzRegisterTable = new HashMap<Integer, Class<? extends ISdfsSerializable>>();
	static final HashMap<Class<? extends ISdfsSerializable>, Integer> typeRegisterTable = new HashMap<Class<? extends ISdfsSerializable>, Integer>();

	static {
		// 在这里注册序列化对象
		register(1, AddNewFileRequest.class);
		register(2, AddNewFileResponse.class);
	}

	public static final synchronized void register(int type, Class<? extends ISdfsSerializable> clazz) {
		if (clazzRegisterTable.containsKey(type)) {
			if (clazz == clazzRegisterTable.get(clazz)) {
				return;
			}
			throw new RuntimeException("The type(" + type +
					") was already registered by class: " + clazzRegisterTable.get(clazz) +
					", can not be replaced by class: " + clazz);
		}
		clazzRegisterTable.put(type, clazz);
		typeRegisterTable.put(clazz, type);
	}

	public static final Class<? extends ISdfsSerializable> getClazz(int type) {
		Class<? extends ISdfsSerializable> clazz = clazzRegisterTable.get(type);
		if (clazz == null) {
			throw new RuntimeException("Unknown serialization type: " + type);
		}
		return clazz;
	}

	public static final int getType(Class<? extends ISdfsSerializable> clazz) {
		Integer type = typeRegisterTable.get(clazz);
		if (type == null) {
			throw new RuntimeException("Unknown serialization class: " + clazz);
		}
		return type;
	}
}
