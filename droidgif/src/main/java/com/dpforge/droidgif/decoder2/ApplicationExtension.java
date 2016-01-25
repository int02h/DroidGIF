package com.dpforge.droidgif.decoder2;


import java.io.IOException;

class ApplicationExtension extends DecoderReader {
	private String mApplicationId;
	private String mApplicationCode;

	ApplicationExtension() {
	}

	@SuppressWarnings("unused")
	public String applicationId() {
		return mApplicationId;
	}

	@SuppressWarnings("unused")
	public String applicationCode() {
		return mApplicationCode;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		final int size = stream.readByte();
		if (size != 11) {
			throw new DecoderException(
					DecoderException.ERROR_WRONG_VALUE,
					"Application extension's block size must be 11 bytes. But was " + size + "."
			);
		}

		mApplicationId = stream.readASCIIString(8);
		mApplicationCode = stream.readASCIIString(3);
		new SubBlocksInputStream(stream).skipAll();
	}
}
