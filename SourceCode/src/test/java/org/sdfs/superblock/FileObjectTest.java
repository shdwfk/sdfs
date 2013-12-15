package org.sdfs.superblock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

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

@RunWith(OnamiRunner.class)
@GuiceModules(TestCaseGuiceModule.class)
public class FileObjectTest extends TestCaseBase {
	private final Random random = new Random(System.currentTimeMillis());

	@Inject
	private ISuperBlock superBlock;

	@Test
	public void testFileRW() throws SdfsException, IOException {
		long fileKey = 1L;

		IFileObject fileObject = superBlock.getFileObject(fileKey);

		// 创建并写文件
		int size = random.nextInt(1024 * 1024);
		OutputStream os = fileObject.createFile(null);
		MockInputStream mockInputStream = new MockInputStream(size);
		int copySize = IOUtils.copy(mockInputStream, os);
		assertEquals(size, copySize);
		os.close();
		mockInputStream.close();

		// 再次写入文件，抛出异常
		try {
			os = fileObject.createFile(null);
			assertTrue(false);
		} catch (SdfsException e) {}

		// 测试fileExists接口
		assertTrue(fileObject.fileExists());

		// 测试getFileSize接口
		assertEquals(size, fileObject.getFileSize());

		// 读文件并check
		InputStream is = fileObject.openFile();
		verifyInputStream(size, mockInputStream.getAdler32(), is);
		is.close();

		// 再次读取文件并check
		is = fileObject.openFile();
		verifyInputStream(size, mockInputStream.getAdler32(), is);
		is.close();

		// 读取 FileMeta，应该是null
		assertNull(fileObject.getFileMeta());

		// 更新 FileMeta
		fileObject.updateFileMeta(new FileMeta());

		// 读取 FileMeta，不再是null
		assertNotNull(fileObject.getFileMeta());

		// 删除文件
		fileObject.delete();

		// 测试fileExists接口
		assertFalse(fileObject.fileExists());

		// 测试getFileSize接口：对于不存在或者已删除文件，getFileSize返回0
		assertEquals(0, fileObject.getFileSize());

		// 已删除文件，读操作会抛出异常
		try {
			is = fileObject.openFile();
			assertTrue(false);
		} catch (SdfsException e) {}

		// 已删除文件，取FileMeta会抛出异常
		try {
			fileObject.getFileMeta();
			assertTrue(false);
		} catch (SdfsException e) {}

		// 已删除文件，不可以再次创建并写文件
		size = random.nextInt(1024 * 1024);
		os = fileObject.createFile(null);
		mockInputStream = new MockInputStream(size);
		copySize = IOUtils.copy(mockInputStream, os);
		assertEquals(size, copySize);
		try {
			os.close();
			assertTrue(false);
		} catch (IOException e) {}
		mockInputStream.close();

//		// 再次读文件并check
//		is = fileObject.openFile();
//		verifyInputStream(size, mockInputStream.getAdler32(), is);
//		is.close();
	}

	public void testFileNotExists() throws SdfsException {
		long fileKey = 2L;

		IFileObject fileObject = superBlock.getFileObject(fileKey);

		// 测试fileExists接口
		assertFalse(fileObject.fileExists());

		// 测试getFileMeta接口，会抛出异常
		try {
			fileObject.getFileMeta();
			assertTrue(false);
		} catch (SdfsException e) {}

		// 测试getFileSize接口：对于不存在或者已删除文件，getFileSize返回0
		assertEquals(0, fileObject.getFileSize());

		// 测试updateFileMeta接口，会抛出异常
		try {
			fileObject.updateFileMeta(null);
			assertTrue(false);
		} catch (SdfsException e) {}

		// 测试updateFileMeta接口，会抛出异常
		try {
			fileObject.openFile();
			assertTrue(false);
		} catch (SdfsException e) {}
	}
}
