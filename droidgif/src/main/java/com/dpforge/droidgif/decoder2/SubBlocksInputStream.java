package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class SubBlocksInputStream {
	private final BinaryStream mStream;

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
		return 0;
	}
}
