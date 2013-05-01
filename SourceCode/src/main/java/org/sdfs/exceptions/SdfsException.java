package org.sdfs.exceptions;

public class SdfsException extends Exception {

	private static final long serialVersionUID = 6977942286268143998L;

	public SdfsException() {
		super();
	}

	public SdfsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SdfsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SdfsException(String message) {
		super(message);
	}

	public SdfsException(Throwable cause) {
		super(cause);
	}

}
