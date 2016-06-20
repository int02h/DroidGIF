package com.dpforge.droidgif.decoder;

import java.util.ArrayList;
import java.util.List;

public class GIFImage {
	private ColorTable mGlobalColorTable;
	private final FrameDecoder mFrameDecoder = new FrameDecoder();

	private int mFramesCount = 0;
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
		return mFramesCount == 0;
	}

	public int framesCount() {
		return mFramesCount;
	}

	public FrameDecoder frameDecoder() {
		return mFrameDecoder;
	}

	public int backgroundColor() {
		return (mGlobalColorTable != null
				? mGlobalColorTable.getColor(mBackgroundColorIndex)
				: 0x000000);
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

	void addFrame(final GIFImageFrame frame) {
		mFramesCount++;
		mFrameDecoder.addFrameForDecoding(frame);
	}

	void finishDecoding() {
		mFrameDecoder.stop();
	}
}
