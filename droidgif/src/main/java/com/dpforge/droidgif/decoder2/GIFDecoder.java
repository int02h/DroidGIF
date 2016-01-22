package com.dpforge.droidgif.decoder2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GIFDecoder {
	private final static String GIF_SIGNATURE = "GIF";
	private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Arrays.asList("87a", "89a"));
	private final BinaryStream mStream;

	public GIFDecoder(final InputStream inputStream) {
		mStream = new BinaryStream(inputStream);
	}

	void decode() throws IOException, DecoderException {
		readHeader();
		readLogicalScreen();
	}

	private void readHeader() throws IOException, DecoderException {
		final String signature = mStream.readASCIIString(3);
		if (!GIF_SIGNATURE.equals(signature))
			throw new DecoderException(DecoderException.ERROR_WRONG_SIGNATURE);

		final String version = mStream.readASCIIString(3);
		if (!SUPPORTED_VERSIONS.contains(version))
			throw new DecoderException(DecoderException.ERROR_UNSUPPORTED_VERSION);
	}

	private void readLogicalScreen() throws IOException, DecoderException {
		final LogicalScreenDecoder decoder = new LogicalScreenDecoder();
		decoder.read(mStream);
	}
}
