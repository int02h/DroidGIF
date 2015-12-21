package com.dpforge.droidgif.datastream;

public class InvalidDataStreamException extends Exception {
	public final static int ERROR_WRONG_SIGNATURE = 0xe001;
	private final int mCode;

	InvalidDataStreamException(final int code) {
		mCode = code;
	}
}
