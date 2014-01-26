package org.sdfs.io.rpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.io.SdfsSerializationRegistry;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.client.RequestRpcClient;
import org.sdfs.io.rpc.server.RequestRpcServer;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;



public class RequestRpcServiceParallelTest extends TestCaseBase {
	@BeforeClass
	public static void beforeClass() {
		SdfsSerializationRegistry.register(-5, CalculateRequest.class);
		SdfsSerializationRegistry.register(-6, CalculateResponse.class);
	}

	private String host = "127.0.0.1";
	private int port = 8080;

	private Thread serverThread;

	@Before
	public void startRpcService() throws Exception {
		serverThread = new RequestRpcServer(10, 20, new IRequestInvokeAdaptor() {
			
			@Override
			public IResponse invokeRequest(IRequest request) throws Exception {
				assertEquals(RequestType.TEST_CASE_TYPE, request.getRequestType());
				CalculateRequest calculateRequest = (CalculateRequest) request;

				int result;
				switch (calculateRequest.getOperator()) {
				case CalculateRequest.OPERATOR_ADD:
					result = calculateRequest.getOperandLeft()
									+ calculateRequest.getOperandRight();
					break;
				case CalculateRequest.OPERATOR_DIV:
					result = calculateRequest.getOperandLeft()
									/ calculateRequest.getOperandRight();
					break;
				case CalculateRequest.OPERATOR_MINUS:
					result = calculateRequest.getOperandLeft()
									- calculateRequest.getOperandRight();
					break;
				case CalculateRequest.OPERATOR_MUL:
					result = calculateRequest.getOperandLeft()
									* calculateRequest.getOperandRight();
					break;
				default:
					throw new RuntimeException("Unknown operator: "
									+ calculateRequest.getOperator());
				}
				return new CalculateResponse(result);
			}
		}).asyncStartRequestRpcServer(port);
		Thread.sleep(2000);
	}

	@After
	public void stopRpcService() {
		serverThread.interrupt();
	}

	private class RpcAccessThread extends Thread {
		RequestRpcClient client;
		int accessCount;
		boolean result = true;

		public RpcAccessThread(RequestRpcClient client, int accessCount) {
			this.client = client;
			this.accessCount = accessCount;
		}

		@Override
		public void run() {
			for (int i = 0; result && i < accessCount; ++ i) {
				int operandLeft = random.nextInt();
				int operandRight = i;
				CalculateRequest request;
				switch (i % 4) {
				case 0:
					request = CalculateRequest.createDivRequest(operandLeft, operandRight);
					break;
				case 1:
					request = CalculateRequest.createAddRequest(operandLeft, operandRight);
					break;
				case 2:
					request = CalculateRequest.createMinusRequest(operandLeft, operandRight);
					break;
				case 3:
					request = CalculateRequest.createMulReqeuest(operandLeft, operandRight);
					break;
				default:
					result = false;
					throw new RuntimeException();
				}
				Future<IResponse> rpcCall = client.rpcCall(request);
				CalculateResponse response = null;
				ExecutionException exception = null;
				
				try {
					response = (CalculateResponse) rpcCall.get();
				} catch (ExecutionException e) {
					exception = e;
				} catch (Exception e) {
					result = false;
					throw new RuntimeException(e);
				}

				try {
					int expectedResult = request.expectedResult();
					if (expectedResult != response.getResult()) {
						result = false;
						throw new RuntimeException();
					}
				} catch (Exception e) {
					if (response != null || exception == null) {
						result = false;
						throw new RuntimeException(e);
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private RpcAccessThread[] createClientAndAccess(int parallel,
			int eachAccessCount) throws Exception {
		RequestRpcClient client = new RequestRpcClient().connect(host, port);
		RpcAccessThread[] threads = new RpcAccessThread[parallel];
		for (int i = 0; i < parallel; ++ i) {
			threads[i] = new RpcAccessThread(client, eachAccessCount);
			threads[i].start();
		}
		return threads;
	}

	@Test
	public void test() throws Exception {
		int nClient = 10;
		int parallel = 10;
		int eachAccessCount = 100;

		RpcAccessThread[][] threadsArray = new RpcAccessThread[nClient][];

		for (int i = 0; i < nClient; ++ i) {
			RpcAccessThread[] threads =
					createClientAndAccess(parallel, eachAccessCount);
			threadsArray[i] = threads;
		}

		for (RpcAccessThread[] threads : threadsArray) {
			for (RpcAccessThread thread : threads) {
				thread.join();
				assertTrue(thread.result);
			}
			threads[0].client.close();
		}
	}
}
