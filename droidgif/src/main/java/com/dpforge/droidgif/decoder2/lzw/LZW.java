package com.dpforge.droidgif.decoder2.lzw;

import java.io.IOException;
import java.io.InputStream;

public class LZW {
	private final static int MAX_SIZE = 4096;

	private LZW() {
	}

	public static int decompress(final InputStream inputStream, final int minCodeSize, final byte[] indexStream) throws IOException {
		int codeSize = minCodeSize;
		final CodeStream codeStream = new CodeStream(inputStream, codeSize);

		int code;
		int clearCode = 1 << (minCodeSize - 1);
		int endCode = clearCode + 1;
		int lastIndex = endCode;
		int prevCode = -1;
		int threshold = (1 << codeSize);
		int resultIndex = 0;

		short[] prefix = new short[MAX_SIZE];
		byte[] suffix = new byte[MAX_SIZE];

		for (code = 0; code < clearCode; ++code) {
			suffix[code] = (byte) code;
			prefix[code] = -1;
		}

		while (true) {
			code = codeStream.nextValue();
			if (code == clearCode) {
				codeSize = minCodeSize;
				codeStream.setCodeSize(codeSize);
				threshold = (1 << codeSize);
				lastIndex = endCode;
				prevCode = codeStream.nextValue();
				indexStream[resultIndex++] = suffix[prevCode];
				continue;
			}
			if (code == endCode) {
				break;
			}

			byte k;
			if (code <= lastIndex) { // code is in the code table
				int tmp = code;
				k = suffix[tmp];
				while (tmp >= 0) {
					k = suffix[tmp];
					indexStream[resultIndex++] = k;
					tmp = prefix[tmp];
				}
			} else { // not in code table
				int tmp = prevCode;
				k = suffix[tmp];
				while (tmp >= 0) {
					k = suffix[tmp];
					indexStream[resultIndex++] = k;
					tmp = prefix[tmp];
				}
				indexStream[resultIndex++] = k;
			}

			lastIndex++;
			prefix[lastIndex] = (short) prevCode;
			suffix[lastIndex] = k;

			if (lastIndex + 1 >= threshold && threshold < MAX_SIZE) {
				threshold <<= 1;
				codeSize++;
				codeStream.setCodeSize(codeSize);
			}
			prevCode = code;
		}

		return resultIndex;
	}
}
