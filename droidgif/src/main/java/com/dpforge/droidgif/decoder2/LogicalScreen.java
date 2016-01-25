package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class LogicalScreen extends DecoderReader {
	private int mWidth;
	private int mHeight;
	private boolean mHasGlobalColorTable;
	private int mColorResolution;
	private boolean mIsGlobalTableSorted;
	private int mBackgroundColorIndex;
	private int mPixelAspectRatio;

	private ColorTable mGlobalColorTable;

	LogicalScreen() {
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public boolean hasGlobalColorTable() {
		return mHasGlobalColorTable;
	}

	public boolean isGlobalTableSorted() {
		return mIsGlobalTableSorted;
	}

	public ColorTable globalColorTable() {
		return mGlobalColorTable;
	}

	public int backgroundColorIndex() {
		return mBackgroundColorIndex;
	}

	public int colorResolution() {
		return mColorResolution;
	}

	public int pixelAspectRatio() {
		return mPixelAspectRatio;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		mWidth = stream.readInt16();
		mHeight = stream.readInt16();

		int flag = stream.readByte();
		mHasGlobalColorTable = ((flag & 0x80) == 0x80);
		mColorResolution = ((flag & 0x70) >> 4);
		mIsGlobalTableSorted = ((flag & 0x08) == 0x08);
		final int globalColorTableSize = (flag & 0x07);

		mBackgroundColorIndex = stream.readByte();
		mPixelAspectRatio = stream.readByte();

		if (mHasGlobalColorTable) {
			int size = (int) Math.pow(2, globalColorTableSize + 1);
			mGlobalColorTable = new ColorTable(size);
			mGlobalColorTable.read(stream);
		}
	}
}
