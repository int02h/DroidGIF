package com.dpforge.droidgif.decoder2;

import java.io.IOException;

abstract class BaseDecoder {
	protected BaseDecoder() {
	}

	abstract void read(final BinaryStream stream) throws IOException, DecoderException;
}
