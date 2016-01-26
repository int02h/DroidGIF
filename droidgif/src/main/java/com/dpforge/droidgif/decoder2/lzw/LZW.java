package com.dpforge.droidgif.decoder2.lzw;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LZW {
	private LZW() {
	}

	public static List<Integer> decompress(final InputStream inputStream, final int minCodeSize, final int colorAmount) throws IOException {
		int codeSize = minCodeSize;
		final CodeTable codeTable = new CodeTable(colorAmount);
		final CodeStream codeStream = new CodeStream(inputStream, codeSize);
		final List<Integer> indexStream = new ArrayList<>();

		int prevCode = -1;
		int threshold = (1 << codeSize);
		while (true) {
			int code = codeStream.nextValue();
			if (code == codeTable.clearCode()) {
				codeTable.init();
				codeSize = minCodeSize;
				threshold = (1 << codeSize);
				codeStream.setCodeSize(minCodeSize);
				prevCode = codeStream.nextValue();
				indexStream.addAll(codeTable.get(prevCode));
			} else if (code == codeTable.endCode() || code < 0) {
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
					k.addAll(0, codeTable.get(prevCode));
					indexStream.addAll(k);
					codeTable.add(k);
				}

				if (codeTable.size() >= threshold) {
					threshold <<= 1;
					codeSize++;
					codeStream.setCodeSize(codeSize);
				}
				prevCode = code;
			}
		}

		return indexStream;
	}
}
