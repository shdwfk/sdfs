package org.sdfs.io.rpc;

import java.io.IOException;

import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.io.SdfsSerializationHelper;

public class RpcExceptionTest extends TestCaseBase {
	@Test
	public void testException() throws IOException {
		RpcException rpcException1 = new RpcException();
		RpcException rpcException2 = new RpcException("This is rpc exception 2.");
		RpcException rpcException3 = new RpcException("This is rpc exception 3.",
				new IOException("This is the nested exception"));

		checkException(rpcException1);
		checkException(rpcException2);
		checkException(rpcException3);
	}

	private void checkException(RpcException exception) throws IOException {
		byte[] writeObject = SdfsSerializationHelper.writeObject(exception);
		RpcException exceptionCopy = SdfsSerializationHelper.readObject(writeObject);
		assertEquals(exception.getMessage(), exceptionCopy.getMessage());
		System.out.println("------------------");
		System.out.println("Print exception: ");
		exception.printStackTrace(System.out);
		System.out.println("Print copied exception: ");
		exceptionCopy.printStackTrace(System.out);
		
	}
}
