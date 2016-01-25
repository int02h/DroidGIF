package com.dpforge.droidgif.decoder2;

import java.util.ArrayList;
import java.util.List;

public class GIFImage {
	private ColorTable mGlobalColorTable;
	private List<GIFImageFrame> mFrames = new ArrayList<>(1);

	private int mWidth;
	private int mHeight;
	private int mBackgroundColorIndex;

	GIFImage() {
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public boolean isEmpty() {
		return mFrames.isEmpty();
	}

	public int framesCount() {
		return mFrames.size();
	}

	public int backgroundColor() {
		return (mGlobalColorTable != null
				? mGlobalColorTable.getColor(mBackgroundColorIndex)
				: 0x000000);
	}

	public GIFImageFrame getFrame(final int index) {
		return mFrames.get(index);
	}

	ColorTable globalColorTable() {
		return mGlobalColorTable;
	}

	void setGlobalColorTable(final ColorTable colorTable) {
		mGlobalColorTable = colorTable;
	}

	void setSize(final int width, final int height) {
		mWidth = width;
		mHeight = height;
	}

	void setBackgroundColorIndex(final int backgroundColorIndex) {
		mBackgroundColorIndex = backgroundColorIndex;
	}

	GIFImageFrame addFrame(final TableBasedImage image, final GraphicControlExtension imageExtension) {
		GIFImageFrame frame = new GIFImageFrame(this, image, imageExtension);
		mFrames.add(frame);
		return frame;
	}
}
