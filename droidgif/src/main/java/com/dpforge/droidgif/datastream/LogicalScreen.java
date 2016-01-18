package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

public class LogicalScreen extends DataStreamBlock {
	private int mWidth;
	private int mHeight;
	private boolean mHasGlobalColorTable;
	private int mColorResolution;
	private boolean mIsGlobalTableSorted;
	private int mGlobalColorTableSize;
	private int mBackgroundColorIndex;
	private int mPixelAspectRatio;

	private ColorTable mGlobalColorTable;

	LogicalScreen() {
	}

	@Override
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		mWidth = BinaryUtils.readInt16(is);
		mHeight = BinaryUtils.readInt16(is);

		int flag = BinaryUtils.readByte(is);
		mHasGlobalColorTable = ((flag & 0x80) == 0x80);
		mColorResolution = ((flag & 0x70) >> 4);
		mIsGlobalTableSorted = ((flag & 0x08) == 0x08);
		mGlobalColorTableSize = (flag & 0x07);

		mBackgroundColorIndex = BinaryUtils.readByte(is);
		mPixelAspectRatio = BinaryUtils.readByte(is);

		if (mHasGlobalColorTable) {
			int size = (int) Math.pow(2, mGlobalColorTableSize + 1);
			mGlobalColorTable = new ColorTable(size);
			mGlobalColorTable.read(is);
		}
	}
}
