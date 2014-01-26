package org.sdfs.io.rpc;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.io.AddNewFileRequest;
import org.sdfs.io.AddNewFileResponse;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.response.ResponseType;
import org.sdfs.io.rpc.client.RequestRpcClient;
import org.sdfs.io.rpc.server.RequestRpcServer;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;


/**
 * 测试基于IRequest和IResponse的rpc服务
 * @author wangfk
 *
 */
public class RequestRpcServiceTest extends TestCaseBase {
	private int port = 8080;
	private Thread serverThread;

	@Before
	public void before() throws InterruptedException {
		serverThread = new RequestRpcServer(10, 20, new IRequestInvokeAdaptor() {
			
			@Override
			public IResponse invokeRequest(IRequest request) throws Exception {
				AddNewFileRequest addNewFileRequest = (AddNewFileRequest) request;
				System.out.println("Handle request: " + addNewFileRequest);
				if (addNewFileRequest.getFileData() == null || addNewFileRequest.getFileData().length == 0) {
					throw new SdfsException("The file data can not be null or empty.");
				}
				AddNewFileResponse addNewFileResponse = new AddNewFileResponse();
				addNewFileResponse.setSuccessful(true);
				return addNewFileResponse;
			}
		}).asyncStartRequestRpcServer(port);
		Thread.sleep(2000);
	}

	@After
	public void after() {
		serverThread.interrupt();
	}

	@Test
	public void testSendAndReceive() throws InterruptedException, ExecutionException, IOException {

		AddNewFileRequest addNewFileRequest = new AddNewFileRequest();
		addNewFileRequest.setBlockId(100);
		addNewFileRequest.setFileKey(101);
		addNewFileRequest.setFileData(new byte[1024]);

		RequestRpcClient requestRpcClient = new RequestRpcClient().connect("127.0.0.1", port);

		IResponse response = requestRpcClient.rpcCall(addNewFileRequest).get();
		assertEquals(response.getResponseType(), ResponseType.ADD_NEW_FILE);
		assertTrue(((AddNewFileResponse) response).isSuccessful());

		response = requestRpcClient.rpcCall(addNewFileRequest).get();
		assertEquals(response.getResponseType(), ResponseType.ADD_NEW_FILE);
		assertTrue(((AddNewFileResponse) response).isSuccessful());

		//exception will raised
		addNewFileRequest.setFileData(new byte[0]);
		try {
			response = requestRpcClient.rpcCall(addNewFileRequest).get();
			assertFalse(true);
		} catch (ExecutionException e) {
			assertTrue(true);
			e.printStackTrace();
		}
		requestRpcClient.close();
	}
}
