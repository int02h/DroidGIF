package com.dpforge.droidgif.decoder;

public class GIFImageFrame {
	private final int mLeft;
	private final int mTop;
	private final int mWidth;
	private final int mHeight;
	private final int mDelay;
	private final DisposalMethod mDisposalMethod;

	private final ColorTable mColorTable;
	private final int mMinCodeSize;
	private final int mTransparentColorIndex;

	private byte[] mCompressedData;
	private byte[] mColorIndices;

	GIFImageFrame(final GIFImage gifImage, final TableBasedImage imageData, final GraphicControlExtension imageExtension) {
		mLeft = imageData.left();
		mTop = imageData.top();
		mWidth = imageData.width();
		mHeight = imageData.height();

		mColorTable = imageData.hasLocalColorTable() ? imageData.localColorTable() : gifImage.globalColorTable();
		mCompressedData = imageData.compressedData();
		mMinCodeSize = imageData.minCodeSize();

		if (imageExtension != null) {
			mDelay = imageExtension.delay();
			mDisposalMethod = imageExtension.disposalMethod();
			mTransparentColorIndex = (imageExtension.hasTransparentColor()
					? imageExtension.transparencyColorIndex()
					: -1);
		} else {
			mDelay = 0;
			mDisposalMethod = DisposalMethod.NOT_SPECIFIED;
			mTransparentColorIndex = -1;
		}
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

	public int transparentColorIndex() {
		return mTransparentColorIndex;
	}

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
}
