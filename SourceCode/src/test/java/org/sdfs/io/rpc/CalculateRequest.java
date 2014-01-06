package org.sdfs.io.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Ignore;
import org.sdfs.io.request.IRequest;
import org.sdfs.io.request.RequestType;

@Ignore
public class CalculateRequest implements IRequest {
	public static final int OPERATOR_ADD = 1;
	public static final int OPERATOR_MINUS = 2;
	public static final int OPERATOR_MUL = 3;
	public static final int OPERATOR_DIV = 4;
	
	private int operandLeft;
	private int operandRight;

	private int operator;

	public CalculateRequest() {
	}

	private CalculateRequest(int operandLeft, int operandRight, int operator) {
		this.operandLeft = operandLeft;
		this.operandRight = operandRight;
		this.operator = operator;
	}

	public static CalculateRequest createAddRequest(int operandLeft, int operandRight) {
		return new CalculateRequest(operandLeft, operandRight, OPERATOR_ADD);
	}

	public static CalculateRequest createMinusRequest(int operandLeft, int operandRight) {
		return new CalculateRequest(operandLeft, operandRight, OPERATOR_MINUS);
	}

	public static CalculateRequest createMulReqeuest(int operandLeft, int operandRight) {
		return new CalculateRequest(operandLeft, operandRight, OPERATOR_MUL);
	}

	public static CalculateRequest createDivRequest(int operandLeft, int operandRight) {
		return new CalculateRequest(operandLeft, operandRight, OPERATOR_DIV);
	}

	public int getOperandLeft() {
		return operandLeft;
	}

	public int getOperandRight() {
		return operandRight;
	}

	public int getOperator() {
		return operator;
	}

	@Override
	public void readFrom(DataInput in) throws IOException {
		operandLeft = in.readInt();
		operandRight = in.readInt();
		operator = in.readInt();
	}

	@Override
	public void writeTo(DataOutput out) throws IOException {
		out.writeInt(operandLeft);
		out.writeInt(operandRight);
		out.writeInt(operator);
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.TEST_CASE_TYPE;
	}

	public int expectedResult() {
		switch (operator) {
			case OPERATOR_ADD:
				return operandLeft + operandRight;
			case OPERATOR_DIV:
				return operandLeft / operandRight;
			case OPERATOR_MINUS:
				return operandLeft - operandRight;
			case OPERATOR_MUL:
				return operandLeft * operandRight;
			default:
				throw new RuntimeException("Unknown operator: " + operator);
		}
	}

	@Override
	public String toString() {
		String str;
		switch (operator) {
		case OPERATOR_ADD:
			str = operandLeft + " + " + operandRight;
			break;
		case OPERATOR_DIV:
			str = operandLeft + " / " + operandRight;
			break;
		case OPERATOR_MINUS:
			str = operandLeft + " - " + operandRight;
			break;
		case OPERATOR_MUL:
			str = operandLeft + " * " + operandRight;
			break;
		default:
			throw new RuntimeException("Unknown operator: " + operator);
		}

		try {
			int expectedResult = expectedResult();
			return str + " = " + expectedResult;
		} catch (Exception e) {
			return str + " = " + e.getClass().getSimpleName()
					+ "(" + e.getMessage() + ")";
		}
	}
	
}
