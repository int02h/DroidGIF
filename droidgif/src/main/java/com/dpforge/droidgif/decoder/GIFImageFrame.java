package com.dpforge.droidgif.decoder;

public class GIFImageFrame {
	private final int mLeft;
	private final int mTop;
	private final int mWidth;
	private final int mHeight;
	private final int mDelay;
	private final DisposalMethod mDisposalMethod;

	private final ColorTable mColorTable;
	private final byte[] mCompressedData;
	private final int mMinCodeSize;
	private final int mTransparentColorIndex;

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

	public byte[] compressedData() {
		return mCompressedData;
	}

	public int minCodeSize() {
		return mMinCodeSize;
	}
}
