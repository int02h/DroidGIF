package com.dpforge.droidgif.decoder.lzw;

import java.io.IOException;
import java.io.InputStream;

public class LZW {
	private final static int MAX_SIZE = 4096;

	private final short[] prefix = new short[MAX_SIZE];
	private final byte[] suffix = new byte[MAX_SIZE];
	private final byte[] stack = new byte[MAX_SIZE + 1];

	public int decompress(final InputStream inputStream, final int minCodeSize, final byte[] indexStream) throws IOException {
		int codeSize = minCodeSize;
		final CodeStream codeStream = new CodeStream(inputStream, codeSize);

		int code;
		int clearCode = 1 << (minCodeSize - 1);
		int endCode = clearCode + 1;
		int lastIndex = endCode + 1;
		int prevCode = -1;
		int threshold = (1 << codeSize);
		int resultIndex = 0;

		for (code = 0; code < clearCode; ++code) {
			suffix[code] = (byte) code;
			prefix[code] = -1;
		}

		int stackTop = 0;

		while (true) {
			code = codeStream.nextValue();
			if (code == clearCode) {
				codeSize = minCodeSize;
				codeStream.setCodeSize(codeSize);
				threshold = (1 << codeSize);
				lastIndex = endCode + 1;
				prevCode = codeStream.nextValue();
				indexStream[resultIndex++] = suffix[prevCode];
				continue;
			}
			if (code == endCode) {
				break;
			}

			byte k;
			if (code < lastIndex) { // code is in the code table
				int tmp = code;
				k = suffix[tmp];
				while (tmp >= 0) {
					k = suffix[tmp];
					stack[stackTop++] = k;
					tmp = prefix[tmp];
				}
				while (stackTop > 0) {
					indexStream[resultIndex++] = stack[--stackTop];
				}
			} else { // not in code table
				int tmp = prevCode;
				k = suffix[tmp];
				while (tmp >= 0) {
					k = suffix[tmp];
					stack[stackTop++] = k;
					tmp = prefix[tmp];
				}
				while (stackTop > 0) {
					indexStream[resultIndex++] = stack[--stackTop];
				}
				indexStream[resultIndex++] = k;
			}

			if (lastIndex < MAX_SIZE) {
				prefix[lastIndex] = (short) prevCode;
				suffix[lastIndex] = k;
				lastIndex++;

				if (lastIndex >= threshold && lastIndex < MAX_SIZE) {
					threshold <<= 1;
					codeSize++;
					codeStream.setCodeSize(codeSize);
				}
			}
			prevCode = code;
		}

		return resultIndex;
	}
}
