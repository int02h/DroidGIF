package com.dpforge.droidgif.decoder2;

import java.util.ArrayList;
import java.util.List;

public class GIFImage {
	private ColorTable mGlobalColorTable;
	private List<GIFImageFrame> mFrames = new ArrayList<>(1);

	GIFImage() {
	}

	ColorTable globalColorTable() {
		return mGlobalColorTable;
	}

	public boolean isEmpty() {
		return mFrames.isEmpty();
	}

	public int framesCount() {
		return mFrames.size();
	}

	public GIFImageFrame getFrame(final int index) {
		return mFrames.get(index);
	}

	void setGlobalColorTable(final ColorTable colorTable) {
		mGlobalColorTable = colorTable;
	}

	GIFImageFrame addFrame(final TableBasedImage image, final GraphicControlExtension imageExtension) {
		GIFImageFrame frame = new GIFImageFrame(this, image, imageExtension);
		mFrames.add(frame);
		return frame;
	}
}
