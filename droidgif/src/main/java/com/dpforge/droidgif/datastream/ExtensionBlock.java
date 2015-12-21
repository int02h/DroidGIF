package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

abstract class ExtensionBlock extends DataStreamBlock {
	final static int LABEL = 0x21;
	final static int LABEL_APPLICATION = 0xFF;
	final static int LABEL_GRAPHIC_CONTROL = 0xF9;

	static ExtensionBlock createBlock(final InputStream is) throws IOException, InvalidDataStreamException {
		final int label = BinaryUtils.readByte(is);
		switch (label) {
			case LABEL_APPLICATION:
				return new ApplicationExtension();
			case LABEL_GRAPHIC_CONTROL:
				return new GraphicControlExtension();
			default:
				throw new InvalidDataStreamException(
						InvalidDataStreamException.ERROR_UNSUPPORTED_EXTENSION,
						"Extension label " + label
				);
		}
	}
}
