package com.dpforge.droidgif.decoder2;

public class GIFImage {
	private ColorTable mGlobalColorTable;

	GIFImage() {
	}

	ColorTable getGlobalColorTable() {
		return mGlobalColorTable;
	}

	void setGlobalColorTable(final ColorTable colorTable) {
		mGlobalColorTable = colorTable;
	}
}
