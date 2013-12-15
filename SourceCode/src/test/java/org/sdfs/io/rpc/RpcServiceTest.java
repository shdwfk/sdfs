package org.sdfs.io.rpc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.io.request.AddNewFileRequest;
import org.sdfs.io.response.AddNewFileResponse;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.response.ResponseType;
import org.sdfs.io.rpc.client.RpcClient;
import org.sdfs.io.rpc.server.RpcServer;


public class RpcServiceTest extends TestCaseBase {
	private int port = 8080;
	private Thread serverThread;

	@Before
	public void before() throws InterruptedException {
		serverThread = RpcServer.asyncStartRequestRpcServer(port);
		Thread.sleep(2000);
	}

	@After
	public void after() {
		serverThread.interrupt();
	}

	@Test
	public void testSendAndReceive() throws InterruptedException {

		AddNewFileRequest addNewFileRequest = new AddNewFileRequest();
		addNewFileRequest.setBlockId(100);
		addNewFileRequest.setFileKey(101);
		addNewFileRequest.setFileData(new byte[1024]);

		IResponse response = RpcClient.requestInvoke("127.0.0.1", port, addNewFileRequest);
		assertEquals(response.getResponseType(), ResponseType.ADD_NEW_FILE);
		assertTrue(((AddNewFileResponse) response).isSuccessful());

		response = RpcClient.requestInvoke("127.0.0.1", port, addNewFileRequest);
		assertEquals(response.getResponseType(), ResponseType.ADD_NEW_FILE);
		assertTrue(((AddNewFileResponse) response).isSuccessful());
	}
}
