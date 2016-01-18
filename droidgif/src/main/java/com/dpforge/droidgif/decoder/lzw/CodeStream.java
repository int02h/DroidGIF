package com.dpforge.droidgif.decoder.lzw;

class CodeStream {
	private final byte[] mBits;
	private int mCodeSize;
	private int mIndex;

	CodeStream(byte[] data, int codeSize) {
		mCodeSize = codeSize;

		mBits = new byte[data.length*8];
		for (int i = 0; i < data.length; ++i) {
			for (int j = 0; j < 8; ++j) {
				byte bit = getBit(data[i], j);
				mBits[i*8 + j] = bit;
			}
		}
		mIndex = 0;
	}

	void setCodeSize(int size) {
		mCodeSize = size;
	}

	boolean hasData() {
		return mIndex + mCodeSize <= mBits.length;
	}

	int nextValue() {
		int res = 0;
		for (int pos = 0; pos < mCodeSize; ++mIndex, ++pos) {
			res += (mBits[mIndex] << pos);
		}
		return res;
	}

	private static byte getBit(int value, int index) {
		int mask = (1 << index);
		return (byte) (((value & mask) == mask) ? 1 : 0);
	}
}

