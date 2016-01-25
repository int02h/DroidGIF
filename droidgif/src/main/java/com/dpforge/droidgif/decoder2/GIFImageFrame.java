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
	private final List<Integer> mColorIndices;

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
		} else {
			mDelay = 0;
			mDisposalMethod = DisposalMethod.NOT_SPECIFIED;
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

	public int getColor(int x, int y) {
		int colorIndex = mColorIndices.get(y*mWidth + x);
		return mColorTable.getColor(colorIndex);
	}
}
