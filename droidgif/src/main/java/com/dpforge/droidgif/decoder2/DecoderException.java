package com.dpforge.droidgif.decoder2;

public class DecoderException extends Exception {
	public final static int ERROR_WRONG_SIGNATURE       = 0xde001;
	public final static int ERROR_UNSUPPORTED_VERSION   = 0xde002;
	public final static int ERROR_UNSUPPORTED_EXTENSION = 0xde003;
	public final static int ERROR_WRONG_VALUE           = 0xde004;
	public final static int ERROR_TERMINATOR_NOT_ZERO   = 0xde005;
	public final static int ERROR_UNSUPPORTED_BLOCK     = 0xde006;

	private final int mCode;
	private final String mMessage;

	DecoderException(final int code) {
		this(code, null);
	}

	DecoderException(final int code, final String message) {
		mCode = code;
		mMessage = message;
	}
}
