package com.dpforge.droidgif.decoder2.lzw;

import java.io.IOException;
import java.io.InputStream;

class CodeStream {
	private final static int[] BIT_MASKS = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095};

	private final InputStream mStream;
	private int mCodeSize;

	private int mBuffer;
	private int mBufferSize;

	public CodeStream(final InputStream inputStream, final int codeSize) {
		mStream = inputStream;
		mCodeSize = codeSize;
	}

	void setCodeSize(int size) {
		mCodeSize = size;
	}

	int nextValue() throws IOException {
		while (mBufferSize < mCodeSize) {
			mBuffer |= mStream.read() << mBufferSize;
			mBufferSize += 8;
		}
		int value = mBuffer & BIT_MASKS[mCodeSize];
		mBuffer >>= mCodeSize;
		mBufferSize -= mCodeSize;
		return value;
	}
}
