package com.dpforge.droidgif.decoder2;

import java.io.IOException;

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
				throw new IOException("Unexpected end of sub-block stream");
		}

		mBytesLeft--;
		return mStream.readByte();
	}
}
