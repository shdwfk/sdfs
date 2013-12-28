package org.sdfs.io.rpc;

import java.io.IOException;

import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.io.SdfsSerializationHelper;
import org.sdfs.io.request.AddNewFileRequest;
import org.sdfs.io.rpc.RpcMessage;

public class RpcMessageTest extends TestCaseBase {
	
	@Test
	public void test() throws IOException {
		AddNewFileRequest message = new AddNewFileRequest();
		message.setBlockId(random.nextLong());
		message.setFileKey(random.nextLong());
		byte[] bytes = new byte[100];
		random.nextBytes(bytes);
		message.setFileData(bytes);

		RpcMessage<AddNewFileRequest> rpcMessage = new RpcMessage<>(random.nextLong(), message);
		byte[] writeObject = SdfsSerializationHelper.writeObject(rpcMessage);
		RpcMessage<AddNewFileRequest> rpcMessage2 =
				SdfsSerializationHelper.readObject(writeObject);
		assertEquals(rpcMessage, rpcMessage2);

		rpcMessage = new RpcMessage<>(random.nextLong(), new RpcException("whatever", new IOException("test")));
		writeObject = SdfsSerializationHelper.writeObject(rpcMessage);
		RpcMessage<AddNewFileRequest> rpcMessage3 =
				SdfsSerializationHelper.readObject(writeObject);
		assertEquals(rpcMessage, rpcMessage3);
	}
}
