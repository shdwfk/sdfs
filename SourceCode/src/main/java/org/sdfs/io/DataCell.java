package org.sdfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataCell implements ISdfsSerializable {
	private enum DataType {
		VOID{
			@Override
			public Class<?> getDataClassType() {return void.class;}
		},
		BOOLEAN{
			@Override
			public Class<?> getDataClassType() {return boolean.class;}
		},
		BYTE{
			@Override
			public Class<?> getDataClassType() {return byte.class;}
		},
		CHAR{
			@Override
			public Class<?> getDataClassType() {return char.class;}
		},
		SHORT{
			@Override
			public Class<?> getDataClassType() {return short.class;}
		},
		INT{
			@Override
			public Class<?> getDataClassType() {return int.class;}
		},
		FLOAT{
			@Override
			public Class<?> getDataClassType() {return float.class;}
		},
		DOUBLE{
			@Override
			public Class<?> getDataClassType() {return double.class;}
		},
		LONG{
			@Override
			public Class<?> getDataClassType() {return long.class;}
		},
		STRING{
			@Override
			public Class<?> getDataClassType() {return String.class;}
		},
		BOOLEAN_ARRAY{
			@Override
			public Class<?> getDataClassType() {return boolean[].class;}
		},
		BYTE_ARRAY{
			@Override
			public Class<?> getDataClassType() {return byte[].class;}
		},
		CHAR_ARRAY{
			@Override
			public Class<?> getDataClassType() {return char[].class;}
		},
		SHORT_ARRAY{
			@Override
			public Class<?> getDataClassType() {return short[].class;}
		},
		INT_ARRAY{
			@Override
			public Class<?> getDataClassType() {return int[].class;}
		},
		FLOAT_ARRAY{
			@Override
			public Class<?> getDataClassType() {return float[].class;}
		},
		DOUBLE_ARRAY{
			@Override
			public Class<?> getDataClassType() {return double[].class;}
		},
		LONG_ARRAY{
			@Override
			public Class<?> getDataClassType() {return long[].class;}
		},
		STRING_ARRAY{
			@Override
			public Class<?> getDataClassType() {return String[].class;}
		},
		;
		public Class<?> getDataClassType() {
			return void.class;
		}
	}

	private static final Map<Class<?>, DataType> dataTypeMap = new HashMap<>();
	static {
		for (DataType dataType : DataType.values()) {
			dataTypeMap.put(dataType.getDataClassType(), dataType);
		}
	}
	
	private DataType dataType;
	private byte[] dataBuffer;
	private ByteArrayOutputStream outputStream;

	public Class<?> getDataClassType() {
		return dataType.getDataClassType();
	}

	public void setVoid() throws IOException {
		setDataType(DataType.VOID);
		dataBuffer = new byte[0];
	}

	public void getVoid() throws IOException {
		checkDataType(DataType.VOID);
	}

	public boolean getBoolean() throws IOException {
		checkDataType(DataType.BOOLEAN);
		return getDataInput().readBoolean();
	}

	public void setBoolean(boolean value) throws IOException {
		setDataType(DataType.BOOLEAN);
		getDataOutput().writeBoolean(value);
		dumpDataOutputToBuffer();
	}

	public byte getByte() throws IOException {
		checkDataType(DataType.BYTE);
		return getDataInput().readByte();
	}

	public void setByte(byte value) throws IOException {
		setDataType(DataType.BYTE);
		getDataOutput().writeByte(value);
		dumpDataOutputToBuffer();
	}

	public char getChar() throws IOException {
		checkDataType(DataType.CHAR);
		return getDataInput().readChar();
	}

	public void setChar(char value) throws IOException {
		setDataType(DataType.CHAR);
		getDataOutput().writeChar(value);
		dumpDataOutputToBuffer();
	}

	public short getShort() throws IOException {
		checkDataType(DataType.SHORT);
		return getDataInput().readShort();
	}

	public void setShort(short value) throws IOException {
		setDataType(DataType.SHORT);
		getDataOutput().writeShort(value);
		dumpDataOutputToBuffer();
	}

	public int getInt() throws IOException {
		checkDataType(DataType.INT);
		return getDataInput().readInt();
	}

	public void setInt(int value) throws IOException {
		setDataType(DataType.INT);
		getDataOutput().writeInt(value);
		dumpDataOutputToBuffer();
	}

	public long getLong() throws IOException {
		checkDataType(DataType.LONG);
		return getDataInput().readLong();
	}

	public void setLong(long value) throws IOException {
		setDataType(DataType.LONG);
		getDataOutput().writeLong(value);
		dumpDataOutputToBuffer();
	}

	public float getFloat() throws IOException {
		checkDataType(DataType.FLOAT);
		return getDataInput().readFloat();
	}

	public void setFloat(float value) throws IOException {
		setDataType(DataType.FLOAT);
		getDataOutput().writeFloat(value);
		dumpDataOutputToBuffer();
	}

	public double getDouble() throws IOException {
		checkDataType(DataType.DOUBLE);
		return getDataInput().readDouble();
	}

	public void setDouble(double value) throws IOException {
		setDataType(DataType.DOUBLE);
		getDataOutput().writeDouble(value);
		dumpDataOutputToBuffer();
	}

	public String getString() throws IOException {
		checkDataType(DataType.STRING);
		return SdfsSerializationHelper.readStringNull(getDataInput());
	}

	public void setString(String value) throws IOException {
		setDataType(DataType.STRING);
		SdfsSerializationHelper.writeStringNull(getDataOutput(), value);
		dumpDataOutputToBuffer();
	}

	public boolean[] getBooleanArray() throws IOException {
		checkDataType(DataType.BOOLEAN_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		boolean[] value = new boolean[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readBoolean();
		}
		return value;
	}

	public void setBooleanArray(boolean[] value) throws IOException {
		setDataType(DataType.BOOLEAN_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (boolean b : value) {
				dataOutput.writeBoolean(b);
			}
		}
		dumpDataOutputToBuffer();
	}

	public void setByteArray(byte[] value) throws IOException {
		setDataType(DataType.BYTE_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (byte b : value) {
				dataOutput.writeByte(b);
			}
		}
		dumpDataOutputToBuffer();
	}

	public byte[] getByteArray() throws IOException {
		checkDataType(DataType.BYTE_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		byte[] value = new byte[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readByte();
		}
		return value;
	}

	public void setCharArray(char[] value) throws IOException {
		setDataType(DataType.CHAR_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (char c : value) {
				dataOutput.writeChar(c);
			}
		}
		dumpDataOutputToBuffer();
	}

	public char[] getCharArray() throws IOException {
		checkDataType(DataType.CHAR_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		char[] value = new char[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readChar();
		}
		return value;
	}

	public void setShortArray(short[] value) throws IOException {
		setDataType(DataType.SHORT_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (short s : value) {
				dataOutput.writeShort(s);
			}
		}
		dumpDataOutputToBuffer();
	}

	public short[] getShortArray() throws IOException {
		checkDataType(DataType.SHORT_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		short[] value = new short[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readShort();
		}
		return value;
	}

	public void setIntArray(int[] value) throws IOException {
		setDataType(DataType.INT_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (int i : value) {
				dataOutput.writeInt(i);
			}
		}
		dumpDataOutputToBuffer();
	}

	public int[] getIntArray() throws IOException {
		checkDataType(DataType.INT_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		int[] value = new int[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readInt();
		}
		return value;
	}

	public void setLongArray(long[] value) throws IOException {
		setDataType(DataType.LONG_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (long l : value) {
				dataOutput.writeLong(l);
			}
		}
		dumpDataOutputToBuffer();
	}

	public long[] getLongArray() throws IOException {
		checkDataType(DataType.LONG_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		long[] value = new long[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readLong();
		}
		return value;
	}

	public void setFloatArray(float[] value) throws IOException {
		setDataType(DataType.FLOAT_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (float f : value) {
				dataOutput.writeFloat(f);
			}
		}
		dumpDataOutputToBuffer();
	}

	public float[] getFloatArray() throws IOException {
		checkDataType(DataType.FLOAT_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		float[] value = new float[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readFloat();
		}
		return value;
	}

	public void setDoubleArray(double[] value) throws IOException {
		setDataType(DataType.DOUBLE_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (double d : value) {
				dataOutput.writeDouble(d);
			}
		}
		dumpDataOutputToBuffer();
	}

	public double[] getDoubleArray() throws IOException {
		checkDataType(DataType.DOUBLE_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		double[] value = new double[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = dataInput.readDouble();
		}
		return value;
	}

	public void setStringArray(String[] value) throws IOException {
		setDataType(DataType.STRING_ARRAY);
		DataOutput dataOutput = getDataOutput();
		dataOutput.writeBoolean(value == null);
		if (value != null) {
			dataOutput.writeInt(value.length);
			for (String s : value) {
				SdfsSerializationHelper.writeStringNull(dataOutput, s);
			}
		}
		dumpDataOutputToBuffer();
	}

	public String[] getStringArray() throws IOException {
		checkDataType(DataType.STRING_ARRAY);
		DataInput dataInput = getDataInput();
		if (dataInput.readBoolean()) {
			return null;
		}
		int length = dataInput.readInt();
		String[] value = new String[length];
		for (int i = 0; i < length; ++ i) {
			value[i] = SdfsSerializationHelper.readStringNull(dataInput);
		}
		return value;
	}

	public Object getDataObject() throws IOException {
		switch (dataType) {
		case BOOLEAN:
			return getBoolean();
		case BOOLEAN_ARRAY:
			return getBooleanArray();
		case BYTE:
			return getByte();
		case BYTE_ARRAY:
			return getByteArray();
		case CHAR:
			return getChar();
		case CHAR_ARRAY:
			return getCharArray();
		case DOUBLE:
			return getDouble();
		case DOUBLE_ARRAY:
			return getDoubleArray();
		case FLOAT:
			return getFloat();
		case FLOAT_ARRAY:
			return getFloatArray();
		case INT:
			return getInt();
		case INT_ARRAY:
			return getIntArray();
		case LONG:
			return getLong();
		case LONG_ARRAY:
			return getLongArray();
		case SHORT:
			return getShort();
		case SHORT_ARRAY:
			return getShortArray();
		case STRING:
			return getString();
		case STRING_ARRAY:
			return getStringArray();
		case VOID:
			return null;
		default:
			throw new IOException("Invalid data type: " + this.dataType);
		}
	}

	public static DataCell fromDataObject(Class<?> dataTypeClass, Object dataObject) throws IOException {
		DataCell dataCell = new DataCell();
		DataType dataType = dataTypeMap.get(dataTypeClass);
		if (dataType == null) {
			throw new IOException("Unsupported data type: " + dataTypeClass);
		}
		switch (dataType) {
		case BOOLEAN:
			dataCell.setBoolean((boolean) dataObject);
			break;
		case BOOLEAN_ARRAY:
			dataCell.setBooleanArray((boolean[]) dataObject);
			break;
		case BYTE:
			dataCell.setByte((byte) dataObject);
			break;
		case BYTE_ARRAY:
			dataCell.setByteArray((byte[]) dataObject);
			break;
		case CHAR:
			dataCell.setChar((char) dataObject);
			break;
		case CHAR_ARRAY:
			dataCell.setCharArray((char[]) dataObject);
			break;
		case DOUBLE:
			dataCell.setDouble((double) dataObject);
			break;
		case DOUBLE_ARRAY:
			dataCell.setDoubleArray((double[]) dataObject);
			break;
		case FLOAT:
			dataCell.setFloat((float) dataObject);
			break;
		case FLOAT_ARRAY:
			dataCell.setFloatArray((float[]) dataObject);
			break;
		case INT:
			dataCell.setInt((int) dataObject);
			break;
		case INT_ARRAY:
			dataCell.setIntArray((int[]) dataObject);
			break;
		case LONG:
			dataCell.setLong((long) dataObject);
			break;
		case LONG_ARRAY:
			dataCell.setLongArray((long[]) dataObject);
			break;
		case SHORT:
			dataCell.setShort((short) dataObject);
			break;
		case SHORT_ARRAY:
			dataCell.setShortArray((short[]) dataObject);
			break;
		case STRING:
			dataCell.setString((String) dataObject);
			break;
		case STRING_ARRAY:
			dataCell.setStringArray((String[]) dataObject);
			break;
		case VOID:
			dataCell.setVoid();
			break;
		default:
			throw new IOException("Unsupported data type: " + dataType);
		}
		return dataCell;
	}

	private DataInput getDataInput() {
		return new DataInputStream(new ByteArrayInputStream(dataBuffer));
	}

	private DataOutput getDataOutput() {
		outputStream = new ByteArrayOutputStream();
		return new DataOutputStream(outputStream);
	}

	private void dumpDataOutputToBuffer() {
		dataBuffer = outputStream.toByteArray();
	}

	private void checkDataType(DataType dataType) throws IOException {
		if (dataType != this.dataType) {
			throw new IOException(
					"Invalid data type, expected: " + dataType
						+ ", actual: " + this.dataType);
		}
	}

	private void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		dataType = DataType.values()[in.readInt()];
		int dataLen = in.readInt();
		dataBuffer = new byte[dataLen];
		in.readFully(dataBuffer);
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeInt(dataType.ordinal());
		out.writeInt(dataBuffer.length);
		out.write(dataBuffer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(dataBuffer);
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataCell other = (DataCell) obj;
		if (!Arrays.equals(dataBuffer, other.dataBuffer))
			return false;
		if (dataType != other.dataType)
			return false;
		return true;
	}
}
