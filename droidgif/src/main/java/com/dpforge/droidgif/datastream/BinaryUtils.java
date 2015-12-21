package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

class BinaryUtils {
	static String readASCIIString(final InputStream is, final int length) throws IOException {
		byte[] data = new byte[length];
		is.read(data);
		return new String(data);
	}
}
