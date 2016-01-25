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

	public GIFImageFrame getFrame(final int index) {
		return mFrames.get(index);
	}

	void setGlobalColorTable(final ColorTable colorTable) {
		mGlobalColorTable = colorTable;
	}

	GIFImageFrame addFrame(final TableBasedImageDecoder imageDecoder, final GraphicControlExtensionDecoder decoder) {
		GIFImageFrame frame = new GIFImageFrame(this, imageDecoder, decoder);
		mFrames.add(frame);
		return frame;
	}
}
