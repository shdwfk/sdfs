package org.sdfs.exceptions;

public class SdfsClientException extends SdfsException {

	private static final long serialVersionUID = 2723341709708340430L;

	public SdfsClientException() {
		super();
	}

	public SdfsClientException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SdfsClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public SdfsClientException(String message) {
		super(message);
	}

	public SdfsClientException(Throwable cause) {
		super(cause);
	}
}
