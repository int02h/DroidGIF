package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class CommentExtension extends DecoderReader {
	CommentExtension() {
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		new SubBlocksInputStream(stream).skipAll();
	}
}
