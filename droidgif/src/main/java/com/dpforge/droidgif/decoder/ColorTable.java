package com.dpforge.droidgif.decoder;

import java.io.IOException;

public class ColorTable extends DecoderReader {
	private final int mSize;
	private final int[] mColors;

	ColorTable(final int size) {
		mSize = size;
		mColors = new int[size];
	}

	public int size() {
		return mSize;
	}

	public int getColor(int index) {
		return mColors[index];
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		for (int i = 0; i < mSize; ++i) {
			int r = stream.readByte();
			int g = stream.readByte();
			int b = stream.readByte();
			mColors[i] = ((r << 16) + (g << 8) + b);
		}
	}
}
