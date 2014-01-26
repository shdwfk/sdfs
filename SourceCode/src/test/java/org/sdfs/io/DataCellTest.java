package org.sdfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.sdfs.TestCaseBase;

public class DataCellTest extends TestCaseBase {

	@Test
	public void test() throws Exception {
		DataCell dataCell = new DataCell();
		// test set and get boolean value
		boolean booleanValue = random.nextBoolean();
		dataCell.setBoolean(booleanValue);
		testRWDataCell(dataCell);
		assertEquals(booleanValue, dataCell.getBoolean());

		// test set and get byte value
		byte byteValue = (byte) random.nextInt();
		dataCell.setByte(byteValue);
		testRWDataCell(dataCell);
		assertEquals(byteValue, dataCell.getByte());

		// test set and get short value
		short shortValue = (short) random.nextInt();
		dataCell.setShort(shortValue);
		testRWDataCell(dataCell);
		assertEquals(shortValue, dataCell.getShort());

		// test set and get char value
		char charValue = (char) random.nextInt();
		dataCell.setChar(charValue);
		testRWDataCell(dataCell);
		assertEquals(charValue, dataCell.getChar());

		// test set and get int value
		int intValue = random.nextInt();
		dataCell.setInt(intValue);
		testRWDataCell(dataCell);
		assertEquals(intValue, dataCell.getInt());

		// test set and get long value
		long longValue = random.nextLong();
		dataCell.setLong(longValue);
		testRWDataCell(dataCell);
		assertEquals(longValue, dataCell.getLong());

		// test set and get float value
		float floatValue = random.nextFloat();
		dataCell.setFloat(floatValue);
		testRWDataCell(dataCell);
		assertEquals(floatValue, dataCell.getFloat(), 0.000001f);

		// test set and get double value
		double doubleValue = random.nextDouble();
		dataCell.setDouble(doubleValue);
		testRWDataCell(dataCell);
		assertEquals(doubleValue, dataCell.getDouble(), 0.000001d);

		// test set and get String value
		String stringValue = Double.toString(random.nextDouble());
		dataCell.setString(stringValue);
		testRWDataCell(dataCell);
		assertEquals(stringValue, dataCell.getString());

		// test set and get boolean array
		boolean[] booleanArray = new boolean[random.nextInt(128)];
		for (int i = 0; i < booleanArray.length; ++ i) {
			booleanArray[i] = random.nextBoolean();
		}
		dataCell.setBooleanArray(booleanArray);
		testRWDataCell(dataCell);
		assertArrayEquals(booleanArray, dataCell.getBooleanArray());

		// test set and get byte array
		byte[] byteArray = new byte[random.nextInt(128)];
		random.nextBytes(byteArray);
		dataCell.setByteArray(byteArray);
		testRWDataCell(dataCell);
		assertArrayEquals(byteArray, dataCell.getByteArray());

		// test set and get short array
		short[] shortArray = new short[random.nextInt(128)];
		for (int i = 0; i < shortArray.length; ++ i) {
			shortArray[i] = (short) random.nextInt();
		}
		dataCell.setShortArray(shortArray);
		testRWDataCell(dataCell);
		assertArrayEquals(shortArray, dataCell.getShortArray());

		// test set and get char array
		char[] charArray = new char[random.nextInt(128)];
		for (int i = 0; i < charArray.length; ++ i) {
			charArray[i] = (char) random.nextInt();
		}
		dataCell.setCharArray(charArray);
		testRWDataCell(dataCell);
		assertArrayEquals(charArray, dataCell.getCharArray());

		// test set and get int array
		int[] intArray = new int[random.nextInt(128)];
		for (int i = 0; i < intArray.length; ++ i) {
			intArray[i] = random.nextInt(128);
		}
		dataCell.setIntArray(intArray);
		testRWDataCell(dataCell);
		assertArrayEquals(intArray, dataCell.getIntArray());

		// test set and get long array
		long[] longArray = new long[random.nextInt(128)];
		for (int i = 0; i < longArray.length; ++ i) {
			longArray[i] = random.nextLong();
		}
		dataCell.setLongArray(longArray);
		testRWDataCell(dataCell);
		assertArrayEquals(longArray, dataCell.getLongArray());

		// test set and get float array
		float[] floatArray = new float[random.nextInt(128)];
		for (int i = 0; i < floatArray.length; ++ i) {
			floatArray[i] = random.nextFloat();
		}
		dataCell.setFloatArray(floatArray);
		testRWDataCell(dataCell);
		assertArrayEquals(floatArray, dataCell.getFloatArray(), 0.000001f);

		// test set and get double array
		double[] doubleArray = new double[random.nextInt(128)];
		for (int i = 0; i < doubleArray.length; ++ i) {
			doubleArray[i] = random.nextDouble();
		}
		dataCell.setDoubleArray(doubleArray);
		testRWDataCell(dataCell);
		assertArrayEquals(doubleArray, dataCell.getDoubleArray(), 0.000001d);

		// test set and get String array
		String[] stringArray = new String[random.nextInt(128)];
		for (int i = 0; i < stringArray.length; ++ i) {
			stringArray[i] = (i%2 == 0) ? null : Double.toString(random.nextDouble());
		}
		dataCell.setStringArray(stringArray);
		testRWDataCell(dataCell);
		assertArrayEquals(stringArray, dataCell.getStringArray());

		// test set and get null string
		dataCell.setString(null);
		testRWDataCell(dataCell);
		assertNull(dataCell.getString());

		// test set and get null boolean array
		dataCell.setBooleanArray(null);
		testRWDataCell(dataCell);
		assertNull(dataCell.getBooleanArray());
	}

	private void testRWDataCell(DataCell dataCell) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		dataCell.writeTo(dataOutputStream);
		ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		DataCell dataCell2 = new DataCell();
		dataCell2.readFrom(new DataInputStream(byteArrayInputStream));
		assertEquals(dataCell, dataCell2);
	}

	private void assertArrayEquals(boolean[] booleanArray,
			boolean[] booleanArray2) {
		assertEquals(booleanArray.length, booleanArray2.length);
		for (int i = 0; i < booleanArray.length; ++ i) {
			assertEquals(booleanArray[i], booleanArray2[i]);
		}
	}
}
