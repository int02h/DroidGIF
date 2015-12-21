package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

class BinaryUtils {
	static String readASCIIString(final InputStream is, final int length) throws IOException {
		byte[] data = new byte[length];
		is.read(data);
		return new String(data);
	}

	static int readByte(final InputStream is) throws IOException {
		return (is.read() & 0xFF);
	}

	static int readInt16(final InputStream is) throws IOException {
		int low = (is.read() & 0xFF);
		int high = (is.read() & 0xFF);
		return (low + (high << 8)) & 0xFFFF;
	}
}
