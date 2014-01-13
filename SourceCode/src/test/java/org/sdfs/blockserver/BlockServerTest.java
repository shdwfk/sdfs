package org.sdfs.blockserver;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.guice.BlockServerGuice;
import org.sdfs.superblock.IFileObject;
import org.sdfs.superblock.ISuperBlock;

import com.google.inject.Inject;

/**
 * BlockServer的单元测试类
 * @author wangfk
 *
 */
@RunWith(OnamiRunner.class)
@GuiceModules(TestCaseGuiceModule.class)
public class BlockServerTest extends TestCaseBase {

	@Inject
	private IBlockServer blockServer;

	@Before
	public void before() {
		// 因为SuperBlockImpl还未实现，将ISuperBlock的注入类型改为MockSuperBlock
		BlockServerGuice.reset(new TestCaseGuiceModule());
	}

	@Test
	public void testInject() throws SdfsException {
		assertNotNull(blockServer);
		assertEquals(BlockServerImpl.class, blockServer.getClass());
	}

	@Test
	public void testBlockServer() throws Exception {
		blockServer.addNewSuperBlock(1);
		ISuperBlock superBlock1 = blockServer.getSuperBlock(1);
		blockServer.addNewSuperBlock(2);
		ISuperBlock superBlock2 = blockServer.getSuperBlock(2);

		superBlockWR(superBlock1, 100);
		superBlockWR(superBlock2, 200);

		assertEquals(300, blockServer.getAvailableSize());
		assertEquals(300, blockServer.getTotalSize());

		try {
			blockServer.getSuperBlock(3);
			assertTrue(false);
		} catch (SdfsException e) {
		}

		blockServer.removeSuperBlock(1);
		try {
			blockServer.getSuperBlock(1);
			assertTrue(false);
		} catch (SdfsException e) {
		}
		assertEquals(200, blockServer.getAvailableSize());
		assertEquals(200, blockServer.getTotalSize());
	}

	private void superBlockWR(ISuperBlock superBlock, int size) throws IOException, SdfsException {
		long fileId = 1;
		IFileObject fileObject = superBlock.getFileObject(fileId);
		OutputStream os = fileObject.createFile(null);
		MockInputStream is = new MockInputStream(size);
		IOUtils.copy(is, os);
		os.close();
		verifyInputStream(size, is.getAdler32(), fileObject.openFile());
	}
}
