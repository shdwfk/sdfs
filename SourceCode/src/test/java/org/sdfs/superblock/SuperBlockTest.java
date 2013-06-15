package org.sdfs.superblock;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdfs.TestCaseBase;
import org.sdfs.TestCaseGuiceModule;
import org.sdfs.commons.FileMeta;
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
		assertEquals(MockSuperBlock.class, superBlock.getClass());
	}

	@Test
	public void testSuperBlock() throws SdfsException, IOException {
		long key1 = 100L;
		long key2 = 200L;
		long key3 = 300L;

		long size1 = 10000;
		long size2 = 0;

		// 使用key1创建一个文件对象
		IFileObject fileObject1 = superBlock.getFileObject(key1);
		// 创建文件，打开OutputStream；PS，FileMeta是可以传null的
		OutputStream os = fileObject1.createFile(null);
		// 写入文件
		IOUtils.copy(new MockInputStream(size1), os);
		// 关闭文件，文件成功创建
		os.close();

		// 文件数应该等于已经成功创建的文件数
		assertEquals(1, superBlock.getFileCount());
		// getAvailableSize返回值应该是所有文件长度的总和
		assertEquals(size1, superBlock.getAvailableSize());
		// getTotalSize返回值应该是包括已删除文件长度的总和
		assertEquals(size1, superBlock.getTotalSize());

		IFileObject fileObject2 = superBlock.getFileObject(key2);
		// 文件数应该等于已经成功创建的文件数，key2还没有创建，不能算数
		assertEquals(1, superBlock.getFileCount());
		// 创建文件，打开OutputStream；PS，FileMeta是可以传null的
		os = fileObject2.createFile(new FileMeta());
		// 写入文件，文件的长度可以为0
		IOUtils.copy(new MockInputStream(size2), os);
		// 关闭文件，文件成功创建
		os.close();

		// 文件数应该等于已经成功创建的文件数
		assertEquals(2, superBlock.getFileCount());
		// getAvailableSize返回值应该是所有文件长度的总和
		assertEquals(size1 + size2, superBlock.getAvailableSize());
		// getTotalSize返回值应该是包括已删除文件长度的总和
		assertEquals(size1 + size2, superBlock.getTotalSize());

		// 删除key1的文件
		fileObject1.delete();
		// 文件数不包含已删除的文件
		assertEquals(1, superBlock.getFileCount());
		// getAvailableSize返回值应该是所有文件长度的总和
		assertEquals(size2, superBlock.getAvailableSize());
		// getTotalSize返回值应该是包括已删除文件长度的总和
		assertEquals(size1 + size2, superBlock.getTotalSize());

		superBlock.compact();
		// compact之后，getTotalSize不包括已删除文件
		assertEquals(size2, superBlock.getTotalSize());

		// 删除key3的文件
		IFileObject fileObject3 = superBlock.getFileObject(key3);
		assertFalse(fileObject3.fileExists());
		try {
			// 尝试读取不存在的文件，会抛出SdfsException异常
			fileObject3.openFile();
			assertTrue(false);
		} catch (SdfsException e) {
			
		}
		try {
			// 删除不存在的文件，会抛出SdfsException异常
			fileObject3.delete();
			assertTrue(false);
		} catch (SdfsException e) {
			
		}
	}
}
