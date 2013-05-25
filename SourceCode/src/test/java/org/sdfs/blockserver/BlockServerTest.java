package org.sdfs.blockserver;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.exceptions.SdfsException;

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

	@Test
	public void testInject() throws SdfsException {
		assertNotNull(blockServer);
		assertEquals(BlockServerImpl.class, blockServer.getClass());
	}
}
