package com.dpforge.droidgif.datastream;

public class InvalidDataStreamException extends Exception {
	public final static int ERROR_WRONG_SIGNATURE       = 0xe001;
	public final static int ERROR_UNSUPPORTED_EXTENSION = 0xe002;
	public final static int ERROR_WRONG_VALUE           = 0xe003;
	public final static int ERROR_TERMINATOR_NOT_ZERO   = 0xe004;

	private final int mCode;
	private final String mMessage;

	InvalidDataStreamException(final int code) {
		this(code, null);
	}

	InvalidDataStreamException(final int code, final String message) {
		mCode = code;
		mMessage = message;
	}
}
