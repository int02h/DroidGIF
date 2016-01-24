package com.dpforge.droidgif.decoder2;

import org.junit.Test;

import java.io.InputStream;

/**
 * Created by d.popov on 22/01/16.
 */
public class GIFDecoderTest {

	@Test
	public void testCreate() throws Exception {
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

		long startTime = System.currentTimeMillis();
		int count = 5000;
		for (int i = 0; i < count; ++i) {
			final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
			final GIFDecoder decoder = new GIFDecoder(inputStream);
			decoder.decode();
		}
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("Elapsed time " + elapsed + "ms");
		System.out.println("Average time " + (elapsed / count) + "ms");

		System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
	}
}