package com.dpforge.droidgif.decoder;

import org.junit.Test;

import java.io.InputStream;

/**
 * Created by d.popov on 22/01/16.
 */
public class GIFDecoderTest {
	@Test
	public void testCreate() throws Exception {
		long startTime = System.currentTimeMillis();
		int count = 10000;
		for (int i = 0; i < count; ++i) {
			final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
			final GIFDecoder decoder = new GIFDecoder(inputStream);
			decoder.decode();
		}
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("[new] Elapsed time " + elapsed + "ms");
		System.out.println("[new] Average time " + (elapsed / count) + "ms");
	}
}