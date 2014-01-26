package org.sdfs;

import java.util.Arrays;

import org.junit.Assert;
import org.sdfs.io.AddNewFileRequest;
import org.sdfs.io.ISdfsSerializable;
import org.sdfs.io.rpc.RpcException;
import org.sdfs.io.rpc.RpcMessage;

public class SdfsAssert extends Assert {
	protected static void assertEquals(AddNewFileRequest expected, AddNewFileRequest addNewFileRequest) {
		if (expected == addNewFileRequest) {
			return;
		}
		assertEquals(expected.getBlockId(), addNewFileRequest.getBlockId());
		assertEquals(expected.getFileKey(), addNewFileRequest.getFileKey());
		assertTrue(Arrays.equals(expected.getFileData(), addNewFileRequest.getFileData()));
	}

	protected static void assertEquals(RpcException expected, RpcException exception) {
		if (expected == exception) {
			return;
		}
		assertEquals(expected.getMessage(), exception.getMessage());
		assertEquals(expected.getCauseClassName(), exception.getCauseClassName());
	}

	protected static <T extends ISdfsSerializable> void assertEquals(
			RpcMessage<T> expected, RpcMessage<T> message) {
		if (expected == message) {
			return;
		}
		assertEquals(expected.getMessageId(), message.getMessageId());
		assertEquals(expected.hasException(), message.hasException());
		assertEquals(expected.getException(), message.getException());
	}
}
