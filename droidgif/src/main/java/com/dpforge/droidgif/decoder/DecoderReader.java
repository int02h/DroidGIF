package com.dpforge.droidgif.decoder;

import java.io.IOException;

abstract class DecoderReader {
	protected DecoderReader() {
	}

	abstract void read(final BinaryStream stream) throws IOException, DecoderException;
}
