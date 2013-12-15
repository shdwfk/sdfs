package org.sdfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.sdfs.TestCaseBase;
import org.sdfs.io.request.AddNewFileRequest;

public class SdfsSerializationTest extends TestCaseBase {
	@Test
	public void test() throws IOException {
		AddNewFileRequest addNewFileRequest = new AddNewFileRequest();
		addNewFileRequest.setBlockId(random.nextLong());
		addNewFileRequest.setFileKey(random.nextLong());
		byte[] data = new byte[1024];
		random.nextBytes(data);
		addNewFileRequest.setFileData(data);

		byte[] writeObject = SdfsSerializationHelper.writeObject(addNewFileRequest);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SdfsSerializationHelper.writeObject(out, addNewFileRequest);
		assertTrue(Arrays.equals(writeObject, out.toByteArray()));

		AddNewFileRequest addNewFileRequest2 =
				SdfsSerializationHelper.readObject(writeObject);
		assertEquals(addNewFileRequest, addNewFileRequest2);

		AddNewFileRequest addNewFileRequest3 =
				SdfsSerializationHelper.readObject(new ByteArrayInputStream(writeObject));
		assertEquals(addNewFileRequest, addNewFileRequest3);

		
		byte[] writeObjectWithoutLen =
				SdfsSerializationHelper.writeObjectWithoutLen(addNewFileRequest);
		
		out = new ByteArrayOutputStream();
		SdfsSerializationHelper.writeObjectWithoutLen(out, addNewFileRequest);
		assertTrue(Arrays.equals(writeObjectWithoutLen, out.toByteArray()));

		AddNewFileRequest addNewFileRequest4 =
				SdfsSerializationHelper.readObjectWithoutLen(writeObjectWithoutLen);
		assertEquals(addNewFileRequest, addNewFileRequest4);

		AddNewFileRequest addNewFileRequest5 =
				SdfsSerializationHelper.readObjectWithoutLen(new ByteArrayInputStream(writeObjectWithoutLen));
		assertEquals(addNewFileRequest, addNewFileRequest5);
	}

	private void assertEquals(AddNewFileRequest expected, AddNewFileRequest addNewFileRequest) {
		assertEquals(expected.getBlockId(), addNewFileRequest.getBlockId());
		assertEquals(expected.getFileKey(), addNewFileRequest.getFileKey());
		assertTrue(Arrays.equals(expected.getFileData(), addNewFileRequest.getFileData()));
	}
}
