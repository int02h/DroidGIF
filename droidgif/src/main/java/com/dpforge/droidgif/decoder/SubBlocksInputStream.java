package com.dpforge.droidgif.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class SubBlocksInputStream extends InputStream {
	private final BinaryStream mStream;
	private final List<byte[]> mParts;
	private int mPartIndex = 0;
	private int mByteIndex = 0;

	SubBlocksInputStream(final BinaryStream stream, final int framePixelCount) {
		int partsCount = (framePixelCount/255) + 1;
		mStream = stream;
		mParts = new ArrayList<>(partsCount);
	}

	void prepare() throws IOException {
		int size;
		while ((size = mStream.readByte()) > 0) {
			mParts.add(mStream.readBytes(size));
		}
	}

	@Override
	public int read() throws IOException {
		if (mPartIndex >= mParts.size()) return -1;
		byte[] part = mParts.get(mPartIndex);

		if (mByteIndex >= part.length) return -1;
		int b = part[mByteIndex] & 0xFF;

		mByteIndex++;
		if (mByteIndex == part.length) {
			mByteIndex = 0;
			mPartIndex++;
		}

		return b;
	}
}
