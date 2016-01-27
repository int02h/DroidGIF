package com.dpforge.droidgif.decoder2;

import java.util.List;

public class GIFImageFrame {
	private final GIFImage mImage;

	private final int mLeft;
	private final int mTop;
	private final int mWidth;
	private final int mHeight;
	private final int mDelay;
	private final DisposalMethod mDisposalMethod;

	private final ColorTable mColorTable;
	private final byte[] mColorIndices;
	private final int mTransparentColorIndex;

	GIFImageFrame(final GIFImage gifImage, final TableBasedImage imageData, final GraphicControlExtension imageExtension) {
		mImage = gifImage;
		mLeft = imageData.left();
		mTop = imageData.top();
		mWidth = imageData.width();
		mHeight = imageData.height();

		mColorTable = imageData.hasLocalColorTable() ? imageData.localColorTable() : gifImage.globalColorTable();
		mColorIndices = imageData.colorIndices();

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
