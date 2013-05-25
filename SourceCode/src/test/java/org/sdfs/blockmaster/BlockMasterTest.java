package org.sdfs.blockmaster;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;

import com.google.inject.Inject;

/**
 * BlockMaster的单元测试类
 * @author wangfk
 *
 */
@RunWith(OnamiRunner.class)
@GuiceModules(TestCaseGuiceModule.class)
public class BlockMasterTest extends TestCaseBase {

	@Inject
	private IBlockMaster blockMaster;

	@Test
	public void testInject() {
		assertNotNull(blockMaster);
		assertEquals(BlockMasterImpl.class, blockMaster.getClass());
	}
}
