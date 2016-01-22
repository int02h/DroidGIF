package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class ColorTableDecoder extends BaseDecoder {
	private final int mTableSize;
	private final ColorTable mColorTable;

	ColorTableDecoder(final int tableSize) {
		mTableSize = tableSize;
		mColorTable = new ColorTable(tableSize);
	}

	ColorTable colorTable() {
		return mColorTable;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		for (int i = 0; i < mTableSize; ++i) {
			int r = stream.readByte();
			int g = stream.readByte();
			int b = stream.readByte();
			mColorTable.setColor(i, (r << 16) + (g << 8) + b);
		}
	}
}
