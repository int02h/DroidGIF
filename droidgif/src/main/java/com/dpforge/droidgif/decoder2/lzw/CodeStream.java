package com.dpforge.droidgif.decoder2.lzw;

import java.io.IOException;
import java.io.InputStream;

class CodeStream {
	private final InputStream mStream;
	private int mCodeSize;

	private int mCurrentByte;
	private int mBitIndex;

	public CodeStream(final InputStream inputStream, final int codeSize) {
		mStream = inputStream;
		mCodeSize = codeSize;
	}

	void setCodeSize(int size) {
		mCodeSize = size;
	}

	int nextValue() throws IOException {
		int value = 0;
		for (int valueBitIndex = 0; valueBitIndex < mCodeSize; valueBitIndex++, mBitIndex = (mBitIndex + 1) % 8) {
			if (mBitIndex == 0) {
				mCurrentByte = mStream.read();
			}
			boolean bit = getBit(mCurrentByte, mBitIndex);
			value = setBit(value, valueBitIndex, bit);
		}
		return value;
	}

	private static boolean getBit(int value, int index) {
		int mask = (1 << index);
		return ((value & mask) == mask);
	}

	private static int setBit(int value, int index, boolean bit) {
		int mask = (1 << index);
		if (bit) {
			return value | mask;
		} else {
			return value & ~mask;
		}
	}
}
