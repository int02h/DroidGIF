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
	private List<Integer> mColorIndices;

	ImageGraphicBlock(final TableBasedImage image) {
		mImage = image;
	}

	public int width() {
		return mImage.width();
	}

	public int height() {
		return mImage.height();
	}

	public int getColor(int x, int y) {
		final ColorTable colorTable = getColorTable();
		int index = y*width() + x;
		return colorTable.getColor(mColorIndices.get(index));
	}

	void setExtension(final GraphicControlExtension extension) {
		mExtension = extension;
	}

	void setGlobalColorTable(final ColorTable globalColorTable) {
		mGlobalColorTable = globalColorTable;
	}

	void decompress() {
		final ColorTable colorTable = getColorTable();
		mColorIndices = LZW.decompress(mImage.imageData(),
				mImage.getLZWMinCodeSize() + 1, colorTable.size());
	}

	private ColorTable getColorTable() {
		return mImage.hasLocalColorTable() ? mImage.localColorTable() : mGlobalColorTable;
	}
}
