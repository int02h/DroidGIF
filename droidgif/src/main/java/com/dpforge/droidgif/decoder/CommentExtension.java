package com.dpforge.droidgif.decoder;

import java.io.IOException;

class CommentExtension extends DecoderReader {
	CommentExtension() {
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		new SubBlocksInputStream(stream).skipAll();
	}
}
