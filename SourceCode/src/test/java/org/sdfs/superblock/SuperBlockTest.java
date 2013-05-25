package org.sdfs.superblock;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.exceptions.SdfsException;

import com.google.inject.Inject;


/**
 * SuperBlock的单元测试类
 * @author wangfk
 *
 */
@RunWith(OnamiRunner.class)
@GuiceModules(TestCaseGuiceModule.class)
public class SuperBlockTest extends TestCaseBase {

	@Inject
	private ISuperBlock superBlock;
	
	@Test
	public void testInject() throws SdfsException {
		assertNotNull(superBlock);
		assertEquals(SuperBlockImpl.class, superBlock.getClass());
	}
}
