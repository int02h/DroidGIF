package com.dpforge.droidgif.decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SubBlocksInputStream {
	private final BinaryStream mStream;
	private int mBytesLeft = 0;

	SubBlocksInputStream(final BinaryStream stream) {
		mStream = stream;
	}

	void skipAll() throws IOException {
		int size;
		while ((size = mStream.readByte()) > 0) {
			mStream.skipBytes(size);
		}
	}

	int read() throws IOException {
		if (mBytesLeft == 0) {
			mBytesLeft = mStream.readByte();
			if (mBytesLeft == 0)
				return -1;
		}

		mBytesLeft--;
		return mStream.readByte();
	}

	byte[] readAll() throws IOException {
		final List<byte[]> parts = new ArrayList<>(16);
		int size, totalSize = 0;
		while ((size = mStream.readByte()) > 0) {
			parts.add(mStream.readBytes(size));
			totalSize += size;
		}
		final byte[] result = new byte[totalSize];
		int index = 0;
		for (byte[] part : parts) {
			System.arraycopy(part, 0, result, index, part.length);
			index += part.length;
		}
		return result;
	}
}
