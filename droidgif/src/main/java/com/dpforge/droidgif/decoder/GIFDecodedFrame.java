package com.dpforge.droidgif.decoder;

public class GIFDecodedFrame {
	private final int mLeft;
	private final int mTop;
	private final int mWidth;
	private final int mHeight;
	private final int mDelay;
	private final DisposalMethod mDisposalMethod;

	private final int mTransparentColorIndex;
	private final ColorTable mColorTable;
	private final byte[] mColorIndices;

	public GIFDecodedFrame(final GIFImageFrame frame, final byte[] colorIndices) {
		mLeft = frame.left();
		mTop = frame.top();
		mWidth = frame.width();
		mHeight = frame.height();
		mDelay = frame.delay();
		mDisposalMethod = frame.disposalMethod();

		mTransparentColorIndex = frame.transparentColorIndex();
		mColorTable = frame.colorTable();

		mColorIndices = colorIndices;
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

	public DisposalMethod disposalMethod() {
		return mDisposalMethod;
	}

	public boolean isTransparentPixel(int x, int y) {
		int colorIndex = (mColorIndices[y*mWidth + x] & 0xFF);
		return colorIndex == mTransparentColorIndex;
	}

	public int getColor(int x, int y) {
		int colorIndex = mColorIndices[y*mWidth + x] & 0xFF;
		return mColorTable.getColor(colorIndex);
	}
}
