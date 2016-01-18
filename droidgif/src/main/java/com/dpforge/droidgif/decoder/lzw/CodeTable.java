package com.dpforge.droidgif.decoder.lzw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CodeTable {
	private final List<List<Integer>> mData;
	private final int mColorAmount;
	private final int mClearCode;
	private final int mEndCode;

	CodeTable(int colorAmount) {
		mColorAmount = colorAmount;

		mData = new ArrayList<>(mColorAmount + 2);
		mClearCode = mColorAmount;
		mEndCode = mColorAmount + 1;
	}

	int clearCode() {
		return mClearCode;
	}

	int endCode() {
		return mEndCode;
	}

	int getSizeBitsCount() {
		int count = 0;
		int value = mData.size();
		while (value > 0) {
			value >>= 1;
			count++;
		}
		return count;
	}

	void init() {
		mData.clear();
		for (int i = 0; i < mColorAmount; ++i) {
			mData.add(Collections.singletonList(i));
		}
		mData.add(Collections.singletonList(mClearCode));
		mData.add(Collections.singletonList(mEndCode));
	}

	List<Integer> get(int index) {
		return mData.get(index);
	}

	public int size() {
		return mData.size();
	}

	public void add(final List<Integer> entry) {
		mData.add(entry);
	}
}

