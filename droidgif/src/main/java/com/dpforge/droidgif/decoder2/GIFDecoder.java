package com.dpforge.droidgif.decoder2;

import java.io.IOException;
import java.io.InputStream;

public class GIFDecoder extends BaseDecoder {
	private final static String GIF_SIGNATURE = "GIF";
	private final static String[] SUPPORTED_VERSIONS = new String[]{"87a", "89a"};

	private GIFDecoder() {
	}

	public static GIFDecoder create(final InputStream inputStream) throws IOException, DecoderException {
		final GIFDecoder decoder = new GIFDecoder();
		decoder.read(new BinaryStream(inputStream));
		return decoder;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		readHeader(stream);
	}

	private void readHeader(final BinaryStream stream) throws IOException, DecoderException {
		final String signature = stream.readASCIIString(3);
		if (!GIF_SIGNATURE.equals(signature))
			throw new DecoderException(DecoderException.ERROR_WRONG_SIGNATURE);

		final String version = stream.readASCIIString(3);
		for (String supported : SUPPORTED_VERSIONS) {
			if (!supported.equals(version))
				throw new DecoderException(DecoderException.ERROR_UNSUPPORTED_VERSION);
		}
	}
}
