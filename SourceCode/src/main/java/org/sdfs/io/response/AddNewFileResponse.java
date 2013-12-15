package org.sdfs.io.response;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.sdfs.io.request.AddNewFileRequest;

/**
 * 服务器端收到 {@link AddNewFileRequest} 请求，这是服务器的返回类型
 * 
 * @author wangfk
 *
 */
public class AddNewFileResponse implements IResponse {
	/** 上传新文件是不是成功的 */
	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public void readFrom(DataInput in) throws IOException {
		successful = in.readBoolean();
	}

	public void writeTo(DataOutput out) throws IOException {
		out.writeBoolean(successful);
	}

	public ResponseType getResponseType() {
		return ResponseType.ADD_NEW_FILE;
	}

	@Override
	public String toString() {
		return "AddNewFileResponse [successful=" + successful + "]";
	}
}
