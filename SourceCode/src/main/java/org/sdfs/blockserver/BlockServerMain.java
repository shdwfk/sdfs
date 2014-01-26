package org.sdfs.blockserver;

import java.util.ArrayList;
import java.util.List;

import org.sdfs.exceptions.SdfsException;
import org.sdfs.guice.BlockServerGuice;
import org.sdfs.io.rpc.server.RequestRpcServer;


public class BlockServerMain {

	private List<IBlockServer> blockServerList;
	private List<Thread> blockServerRpcServiceThreadList;

	private BlockServerMain(List<IBlockServer> blockServerList,
			List<Thread> blockServerRpcServiceThreadList) {
		this.blockServerList = blockServerList;
		this.blockServerRpcServiceThreadList = blockServerRpcServiceThreadList;
	}

	public List<IBlockServer> getBlockServerList() {
		return blockServerList;
	}

	public List<Thread> getBlockServerRpcServiceThreadList() {
		return blockServerRpcServiceThreadList;
	}

	private void interruptAllRpcThreads() {
		for (Thread thread : blockServerRpcServiceThreadList) {
			thread.interrupt();
		}
	}


	private static final int DEFAUL_REQUEST_EXECUTE_CONCURRENCY = 10;
	private static final int DEFAULT_MESSAGE_HANDLE_CONCURRENCY = 10;

	private static BlockServerMain blockServerMainInstance;
	private static Thread mainThread;

	public static final BlockServerMain getBlockServerMain() {
		return blockServerMainInstance;
	}

	public static Thread getMainThread() {
		return mainThread;
	}

	/**
	 * @param args {port, blocks dir, port, blocks dir ...}
	 * @throws SdfsException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws SdfsException, InterruptedException {
		assert args.length > 0 && args.length % 2 == 0;

		List<IBlockServer> blockServerList = new ArrayList<>();
		List<Thread> blockServerRpcServiceThreadList = new ArrayList<>();
		
		for (int i = 0; i < args.length; i += 2) {
			int port = Integer.parseInt(args[i]);
			String blockDir = args[i + 1];
			new BlockServerGuice();
			IBlockServer blockServer =
					BlockServerGuice.getInjector().getInstance(IBlockServer.class);
			blockServer.init(blockDir);
			Thread blockServerRpcServiceThread = new RequestRpcServer(
					DEFAUL_REQUEST_EXECUTE_CONCURRENCY,
					DEFAULT_MESSAGE_HANDLE_CONCURRENCY,
					new BlockServerRequestInvokerAdaptorV1(blockServer))
					.asyncStartRequestRpcServer(port);
			blockServerList.add(blockServer);
			blockServerRpcServiceThreadList.add(blockServerRpcServiceThread);
		}
		blockServerMainInstance = new BlockServerMain(blockServerList, blockServerRpcServiceThreadList);
		try {
			mainThread = Thread.currentThread();
			mainThread.join();
		} catch (InterruptedException e) {
			// TODO: log interrupted
		}

		blockServerMainInstance.interruptAllRpcThreads();
		// TODO: log server stopped
	}
}
