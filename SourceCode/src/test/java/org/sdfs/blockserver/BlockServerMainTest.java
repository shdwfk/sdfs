package org.sdfs.blockserver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.guice.BlockServerGuice;
import org.sdfs.io.AddNewFileRequest;
import org.sdfs.io.AddNewFileResponse;
import org.sdfs.io.FetchFileRequest;
import org.sdfs.io.FetchFileResponse;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.client.RequestRpcClient;

public class BlockServerMainTest extends TestCaseBase {
	private int port = 8080;
	static {
		BlockServerGuice.reset(new TestCaseGuiceModule());
	}

	@Before
	public void before() throws InterruptedException {
		Thread serverThread = new Thread() {

			@Override
			public void run() {
				try {
					BlockServerMain.main(new String[]{Integer.toString(port), "/block_dir/whatever"});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		};
		serverThread.start();

		Thread.sleep(2000);
		assertNotNull(BlockServerMain.getBlockServerMain());
	}

	@After
	public void after() throws InterruptedException {
		BlockServerMain.getMainThread().interrupt();
		BlockServerMain.getMainThread().join();
	}

	@Test
	public void test() throws SdfsException, InterruptedException, ExecutionException {
		List<IBlockServer> blockServerList =
				BlockServerMain.getBlockServerMain().getBlockServerList();
		assertEquals(1, blockServerList.size());

		IBlockServer blockServer = blockServerList.get(0);
		blockServer.addNewSuperBlock(1);

		byte[] fileData = new byte[1024];
		random.nextBytes(fileData);

		RequestRpcClient requestRpcClient =
				new RequestRpcClient().setCallTimeOutMillis(1000)
					.setCleanCallFutureIntervalMills(60 * 1000)
					.setMaxRequestBufferSize(32).connect("localhost", port);
		AddNewFileRequest addNewFileRequest = new AddNewFileRequest();
		addNewFileRequest.setBlockId(1);
		addNewFileRequest.setFileKey(1);
		addNewFileRequest.setFileData(fileData);

		Future<IResponse> rpcCall = requestRpcClient.rpcCall(addNewFileRequest);
		AddNewFileResponse addNewFileResponse = (AddNewFileResponse) rpcCall.get();
		assertTrue(addNewFileResponse.isSuccessful());

		FetchFileRequest fetchFileRequest = new FetchFileRequest();
		fetchFileRequest.setBlockId(1);
		fetchFileRequest.setFileKey(1);
		Future<IResponse> rpcCall2 = requestRpcClient.rpcCall(fetchFileRequest);
		FetchFileResponse fetchFileResponse = (FetchFileResponse) rpcCall2.get();

		assertTrue(Arrays.equals(fileData, fetchFileResponse.getFileData()));
	}
}
