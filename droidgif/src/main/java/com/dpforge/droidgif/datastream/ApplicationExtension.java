package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

class ApplicationExtension extends ExtensionBlock {
	private String mApplicationId;
	private String mApplicationCode;
	private final DataSubBlocks mApplicationData = new DataSubBlocks();

	@Override
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		final int size = BinaryUtils.readByte(is);
		if (size != 11) {
			throw new InvalidDataStreamException(
					InvalidDataStreamException.ERROR_WRONG_VALUE,
					"Application extension's block size must be 11 bytes. But was " + size + "."
			);
		}

		mApplicationId = BinaryUtils.readASCIIString(is, 8);
		mApplicationCode = BinaryUtils.readASCIIString(is, 3);
		mApplicationData.read(is);
	}
}
