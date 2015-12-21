package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Header in GIF data stream
 */
class Header extends DataStreamBlock {
	private final static String SIGNATURE = "GIF";

	private String mVersion;

	String version() {
		return mVersion;
	}

	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		final String signature = BinaryUtils.readASCIIString(is, 3);
		if (!SIGNATURE.equals(signature))
			throw new InvalidDataStreamException(InvalidDataStreamException.ERROR_WRONG_SIGNATURE);

		mVersion = BinaryUtils.readASCIIString(is, 3);
	}
}
