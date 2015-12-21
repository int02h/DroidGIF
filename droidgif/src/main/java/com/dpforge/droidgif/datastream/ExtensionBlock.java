package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

abstract class ExtensionBlock extends DataStreamEntity {
	final static int LABEL = 0x21;
	final static int LABEL_APPLICATION = 0xFF;

	static ExtensionBlock readBlock(final InputStream is) throws IOException, InvalidDataStreamException {
		final int label = BinaryUtils.readByte(is);
		final ExtensionBlock result;
		switch (label) {
			case LABEL_APPLICATION:
				result = new ApplicationExtension();
				break;
			default:
				throw new InvalidDataStreamException(
						InvalidDataStreamException.ERROR_UNSUPPORTED_EXTENSION,
						"Extension label " + label
				);
		}

		result.read(is);
		return result;
	}
}
