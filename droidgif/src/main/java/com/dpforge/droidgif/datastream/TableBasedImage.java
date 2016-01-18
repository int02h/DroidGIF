package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

public class TableBasedImage extends DataStreamBlock {
	final static int LABEL = 0x2C;

	private int mLeft;
	private int mTop;
	private int mWidth;
	private int mHeight;
	private boolean mHasLocalColorTable;
	private boolean mInterlace;
	private boolean mIsLocalTableSorted;

	private ColorTable mLocalColorTable;

	private int mLZWMinCodeSize;
	private final DataSubBlocks mImageData = new DataSubBlocks();

	TableBasedImage() {
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

	public byte[] imageData() {
		return mImageData.data();
	}

	public int getLZWMinCodeSize() {
		return mLZWMinCodeSize;
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

	public boolean isInterlace() {
		return mInterlace;
	}

	@Override
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		mLeft = BinaryUtils.readInt16(is);
		mTop = BinaryUtils.readInt16(is);
		mWidth = BinaryUtils.readInt16(is);
		mHeight = BinaryUtils.readInt16(is);

		final int flag = BinaryUtils.readByte(is);
		mHasLocalColorTable = ((flag & 0x80) == 0x80);
		mInterlace = ((flag & 0x40) == 0x40);
		mIsLocalTableSorted = ((flag & 0x20) == 0x20);
		final int localColorTableSize = (flag & 0x07);

		if (mHasLocalColorTable) {
			int size = (int) Math.pow(2, localColorTableSize + 1);
			mLocalColorTable = new ColorTable(size);
			mLocalColorTable.read(is);
		}

		mLZWMinCodeSize = BinaryUtils.readByte(is);
		mImageData.read(is);
	}
}
