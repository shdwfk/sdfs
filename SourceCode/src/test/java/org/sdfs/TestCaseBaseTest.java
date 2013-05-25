package org.sdfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.Adler32;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * 用来验证TestCaseBase的单元测试类
 * @author wangfk
 *
 */
public class TestCaseBaseTest extends TestCaseBase {

	/** 用来验证MockInputStream的单元测试方法 */
	@Test
	public void testMockInputStream() throws IOException {
		long size = random.nextInt(1024*1024);

		MockInputStream testInputStream = new MockInputStream(size);
		byte[] data = IOUtils.toByteArray(testInputStream);

		Adler32 adler32 = new Adler32();
		adler32.update(data);

		assertEquals(size, data.length);
		assertEquals(testInputStream.getAdler32(), adler32.getValue());
	}

	/** 用来验证MockOutputSteam的单元测试方法 */
	@Test
	public void testMockOutputStream() throws IOException {
		int size = random.nextInt(1024*1024);
		byte[] data = new byte[size];
		random.nextBytes(data);

		Adler32 adler32 = new Adler32();
		adler32.update(data);

		MockOutputStream testOutputStream = new MockOutputStream();
		IOUtils.write(data, testOutputStream);

		assertEquals(size, testOutputStream.getSize());
		assertEquals(adler32.getValue(), testOutputStream.getAdler32());
	}

	/** 用来验证verifyInputStream方法的单元测试方法 */
	@Test
	public void testVerifyInputStreamMethod() throws IOException {
		int size = random.nextInt(1024*1024);
		byte[] data = new byte[size];

		Adler32 adler32 = new Adler32();
		adler32.update(data);
		
		verifyInputStream(size, adler32.getValue(), new ByteArrayInputStream(data));
	}
}
