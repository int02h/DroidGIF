package com.dpforge.droidgif.decoder;

public class GIFImageFrame {
	private int mLeft = 0;
	private int mTop = 0;
	private int mWidth = 0;
	private int mHeight = 0;
	private int mDelay = 0;
	private DisposalMethod mDisposalMethod = DisposalMethod.NOT_SPECIFIED;

	private ColorTable mColorTable;
	private int mMinCodeSize;
	private int mTransparentColorIndex = -1;

	private byte[] mCompressedData;
	private byte[] mColorIndices;

	GIFImageFrame() {
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

	public int delay() {
		return mDelay;
	}

	@SuppressWarnings("unused")
	public int transparentColorIndex() {
		return mTransparentColorIndex;
	}

	@SuppressWarnings("unused")
	public ColorTable colorTable() {
		return mColorTable;
	}

	public DisposalMethod disposalMethod() {
		return mDisposalMethod;
	}

	public int minCodeSize() {
		return mMinCodeSize;
	}

	public boolean isTransparentPixel(int x, int y) {
		int colorIndex = (mColorIndices[y*mWidth + x] & 0xFF);
		return colorIndex == mTransparentColorIndex;
	}

	public int getColor(int x, int y) {
		int colorIndex = mColorIndices[y*mWidth + x] & 0xFF;
		return mColorTable.getColor(colorIndex);
	}

	byte[] compressedData() {
		return mCompressedData;
	}

	void setDecoded(final byte[] colorIndices) {
		mColorIndices = colorIndices;
		mCompressedData = null;
	}

	void setDisposalMethod(final DisposalMethod disposalMethod) {
		mDisposalMethod = disposalMethod;
	}

	void setTransparentColorIndex(final int index) {
		mTransparentColorIndex = index;
	}

	void setDelay(final int delay) {
		mDelay = delay;
	}

	void setPosition(final int left, final int top) {
		mLeft = left;
		mTop = top;
	}

	void setSize(final int width, final int height) {
		mWidth = width;
		mHeight = height;
	}

	void setColorTable(final ColorTable colorTable) {
		mColorTable = colorTable;
	}

	void setCompressedData(final int minCodeSize, final byte[] data) {
		mMinCodeSize = minCodeSize;
		mCompressedData = data;
	}
}
