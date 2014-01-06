package org.sdfs.superblock;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.Mock;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.exceptions.SdfsException;

@RunWith(OnamiRunner.class)
public class EasyMockTest extends TestCaseBase {

	@Mock(providedBy="getMockObject")
	private ISuperBlock superBlock;

	public static ISuperBlock getMockObject() throws SdfsException {
		ISuperBlock niceMock = EasyMock.createNiceMock(ISuperBlock.class);
		EasyMock.expect(niceMock.getFileObject(0L)).andReturn(null).times(1);
		EasyMock.expect(niceMock.getAvailableSize()).andReturn(100L).times(2);
		EasyMock.replay(niceMock);
		return niceMock;
	}

	@Test
	public void test() throws SdfsException {
		assertNull(superBlock.getFileObject(0L));
		assertEquals(100L, superBlock.getAvailableSize());
		assertEquals(100L, superBlock.getAvailableSize());

		EasyMock.verify(superBlock);
		System.out.println("HAHA!");
	}
}
