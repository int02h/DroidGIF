package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

public class ColorTable extends DataStreamBlock {
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
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		for (int i = 0; i < mSize; ++i) {
			int r = BinaryUtils.readByte(is);
			int g = BinaryUtils.readByte(is);
			int b = BinaryUtils.readByte(is);
			mColors[i] = ((r << 16) + (g << 8) + b);
		}
	}
}