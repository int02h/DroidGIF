package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.ColorTable;
import com.dpforge.droidgif.datastream.GraphicControlExtension;
import com.dpforge.droidgif.datastream.TableBasedImage;
import com.dpforge.droidgif.decoder.lzw.LZW;

import java.util.List;

public class ImageGraphicBlock {
	final private TableBasedImage mImage;
	private GraphicControlExtension mExtension;
	private ColorTable mGlobalColorTable;

	ImageGraphicBlock(final TableBasedImage image) {
		mImage = image;
	}

	void setExtension(final GraphicControlExtension extension) {
		mExtension = extension;
	}

	void setGlobalColorTable(final ColorTable globalColorTable) {
		mGlobalColorTable = globalColorTable;
	}

	void decompress() {
		final ColorTable colorTable = mImage.hasLocalColorTable()
				? mImage.localColorTable()
				: mGlobalColorTable;
		final List<Integer> indices = LZW.decompress(mImage.imageData(),
				mImage.getLZWMinCodeSize() + 1, colorTable.size());
	}
}
