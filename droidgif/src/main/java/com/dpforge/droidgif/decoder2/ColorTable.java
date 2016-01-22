package com.dpforge.droidgif.decoder2;

public class ColorTable {
	private final int mSize;
	private final int[] mColors;

	ColorTable(final int size) {
		mSize = size;
		mColors = new int[size];
	}

	public int size() {
		return mSize;
	}

	public int getColor(int index) {
		return mColors[index];
	}

	void setColor(int index, int color) {
		mColors[index] = color;
	}
}
