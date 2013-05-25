package org.sdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.zip.Adler32;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;

/**
 * TestCase的基类，为所有的TestCase提供共用代码
 * @author wangfk
 *
 */
@Ignore
public class TestCaseBase extends Assert {
	protected static final Random random = new Random(System.currentTimeMillis());


	/** 用于测试的InputStream实现 */
	protected static class MockInputStream extends InputStream {
		private final Adler32 adler32 = new Adler32();

		private long size;

		public MockInputStream(long size) {
			this.size = size;
		}

		@Override
		public int read() throws IOException {
			if (--size < 0) {
				return -1;
			}
			int result = random.nextInt(256);
			adler32.update(result);
			return result;
		}

		public long getAdler32() {
			return adler32.getValue();
		}
	}

	/** 用于测试的OutputStream实现 */
	protected static class MockOutputStream extends OutputStream {
		private final Adler32 adler32 = new Adler32();
		private long size = 0;

		@Override
		public void write(int b) throws IOException {
			adler32.update(b);
			++ size;
		}

		public long getAdler32() {
			return adler32.getValue();
		}

		public long getSize() {
			return size;
		}
	}

	/** 用来验证InputStream内容的静态方法 */
	protected static final void verifyInputStream(long expectedSize,
			long expectedAdler32, InputStream input) throws IOException {
		MockOutputStream output = new MockOutputStream();
		IOUtils.copy(input, output);
		Assert.assertEquals(expectedSize, output.getSize());
		Assert.assertEquals(expectedAdler32, output.getAdler32());
	}
}
