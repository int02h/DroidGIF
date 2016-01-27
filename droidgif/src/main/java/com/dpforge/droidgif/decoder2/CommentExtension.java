package com.dpforge.droidgif.decoder2;

import android.util.Log;

import java.io.IOException;

class CommentExtension extends DecoderReader {
	CommentExtension() {
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		final SubBlocksInputStream subStream = new SubBlocksInputStream(stream);
		int b;
		final StringBuilder builder = new StringBuilder();
		while ((b = subStream.read()) != -1) {
			builder.append((char) (b & 0xFF));
		}
		Log.i("COMMENT", builder.toString());
	}
}
