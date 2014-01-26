package org.sdfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.sdfs.TestCaseBase;

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
				SdfsSerializationHelper.readObjectWithoutLen(
						new ByteArrayInputStream(writeObjectWithoutLen));
		assertEquals(addNewFileRequest, addNewFileRequest5);

		out = new ByteArrayOutputStream();
		SdfsSerializationHelper.writeSerializableObjectNull(
				new DataOutputStream(out), addNewFileRequest);
		AddNewFileRequest addNewFileRequest6 =
				SdfsSerializationHelper.readSerializableObjectNull(
						new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
		assertEquals(addNewFileRequest, addNewFileRequest6);

		out = new ByteArrayOutputStream();
		SdfsSerializationHelper.writeSerializableObjectNull(
				new DataOutputStream(out), null);
		AddNewFileRequest addNewFileRequest7 =
				SdfsSerializationHelper.readSerializableObjectNull(
						new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
		assertNull(addNewFileRequest7);
	}
}
