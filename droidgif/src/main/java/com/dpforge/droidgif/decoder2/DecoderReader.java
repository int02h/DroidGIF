package com.dpforge.droidgif.decoder2;

import java.io.IOException;

abstract class DecoderReader {
	protected DecoderReader() {
	}

	abstract void read(final BinaryStream stream) throws IOException, DecoderException;
}
