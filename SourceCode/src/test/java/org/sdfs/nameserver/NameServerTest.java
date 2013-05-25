package org.sdfs.nameserver;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.exceptions.SdfsException;

import com.google.inject.Inject;

/**
 * NameServer的单元测试类
 * @author wangfk
 *
 */
@RunWith(OnamiRunner.class)
@GuiceModules(TestCaseGuiceModule.class)
public class NameServerTest extends TestCaseBase {

	@Inject
	private INameServer nameServer;

	@Test
	public void testInject() throws SdfsException {
		assertNotNull(nameServer);
		assertEquals(NameServerImpl.class, nameServer.getClass());
	}
}
