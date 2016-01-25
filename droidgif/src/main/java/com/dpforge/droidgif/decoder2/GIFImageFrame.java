package com.dpforge.droidgif.decoder2;

import java.util.List;

public class GIFImageFrame {
	private final GIFImage mImage;

	private final int mLeft;
	private final int mTop;
	private final int mWidth;
	private final int mHeight;
	private final int mDelay;

	private final ColorTable mColorTable;
	private final List<Integer> mColorIndices;

	GIFImageFrame(final GIFImage gifImage, final TableBasedImage imageData, final GraphicControlExtension imageExtension) {
		mImage = gifImage;
		mLeft = imageData.left();
		mTop = imageData.top();
		mWidth = imageData.width();
		mHeight = imageData.height();

		mDelay = imageExtension != null ? imageExtension.delay() : 0;
		mColorTable = imageData.hasLocalColorTable() ? imageData.localColorTable() : gifImage.globalColorTable();
		mColorIndices = imageData.colorIndices();
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

	public int getColor(int x, int y) {
		int colorIndex = mColorIndices.get(y*mWidth + x);
		return mColorTable.getColor(colorIndex);
	}
}
