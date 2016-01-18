package com.dpforge.droidgif.decoder.lzw;

import java.util.ArrayList;
import java.util.List;

public class LZW {
	private LZW() {
	}

	public static List<Integer> decompress(final byte[] data, final int minCodeSize, final int colorAmount) {
		int codeSize = minCodeSize;
		final CodeTable codeTable = new CodeTable(colorAmount);
		final CodeStream codeStream = new CodeStream(data, codeSize);
		final List<Integer> indexStream = new ArrayList<>();

		int prevCode = -1;
		while (codeStream.hasData()) {
			int code = codeStream.nextValue();
			if (code == codeTable.clearCode()) {
				codeTable.init();
				codeSize = minCodeSize;
				codeStream.setCodeSize(minCodeSize);
				prevCode = codeStream.nextValue();
				indexStream.addAll(codeTable.get(prevCode));
			} else if (code == codeTable.endCode()) {
				break;
			} else {
				if (code < codeTable.size()) { // code is in the code table
					indexStream.addAll(codeTable.get(code));
					List<Integer> k = new ArrayList<>();
					k.add(codeTable.get(code).get(0));
					k.addAll(0, codeTable.get(prevCode));
					codeTable.add(k);
				} else { // not in code table
					List<Integer> k = new ArrayList<>();
					k.add(codeTable.get(prevCode).get(0));
					k.addAll(codeTable.get(prevCode));
					indexStream.addAll(k);
					codeTable.add(k);
				}
				codeSize = Math.max(codeSize, codeTable.getSizeBitsCount());
				codeStream.setCodeSize(codeSize);
				prevCode = code;
			}
		}

		return indexStream;
	}
}
