package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class TableBasedImageDecoder extends BaseDecoder {
	private int mLeft;
	private int mTop;
	private int mWidth;
	private int mHeight;
	private boolean mHasLocalColorTable;
	private boolean mInterlace;
	private boolean mIsLocalTableSorted;

	private ColorTable mLocalColorTable;

	TableBasedImageDecoder() {
	}

	public int left() {
		return mLeft;
	}

	public int top() {
		return mTop;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public boolean hasLocalColorTable() {
		return mHasLocalColorTable;
	}

	public boolean isLocalTableSorted() {
		return mIsLocalTableSorted;
	}

	public ColorTable localColorTable() {
		return mLocalColorTable;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		mLeft = stream.readInt16();
		mTop = stream.readInt16();
		mWidth = stream.readInt16();
		mHeight = stream.readInt16();

		final int flag = stream.readByte();
		mHasLocalColorTable = ((flag & 0x80) == 0x80);
		mInterlace = ((flag & 0x40) == 0x40);
		mIsLocalTableSorted = ((flag & 0x20) == 0x20);
		final int localColorTableSize = (flag & 0x07);

		if (mHasLocalColorTable) {
			int size = (int) Math.pow(2, localColorTableSize + 1);
			ColorTableDecoder colorTableDecoder = new ColorTableDecoder(size);
			colorTableDecoder.read(stream);
			mLocalColorTable = colorTableDecoder.colorTable();
		}

		readImageData(stream);
	}

	private void readImageData(final BinaryStream stream) throws IOException {
		final int LZWMinCodeSize = stream.readByte();
		SubBlocksInputStream subStream = new SubBlocksInputStream(stream);
		subStream.skipAll(); // TODO
	}
}
