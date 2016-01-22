package com.dpforge.droidgif.decoder2;

import java.io.IOException;
import java.io.InputStream;

class BinaryStream {
	private final InputStream mStream;

	BinaryStream(final InputStream stream) {
		mStream = stream;
	}

	String readASCIIString(final int length) throws IOException {
		byte[] data = new byte[length];
		mStream.read(data);
		return new String(data);
	}

	int readByte() throws IOException {
		return (mStream.read() & 0xFF);
	}

	byte[] readBytes(int count) throws IOException {
		byte[] data = new byte[count];
		mStream.read(data);
		return data;
	}

	int readInt16() throws IOException {
		int low = readByte();
		int high = readByte();
		return (low + (high << 8)) & 0xFFFF;
	}

	void skipBytes(long count) throws IOException {
		mStream.skip(count);
	}
}
