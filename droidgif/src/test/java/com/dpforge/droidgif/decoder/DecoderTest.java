package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.DataStream;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class DecoderTest {
	@Test
	public void testCreate() throws Exception {
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

		long startTime = System.currentTimeMillis();
		int count = 5000;
		for (int i = 0; i < count; ++i) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
			DataStream dataStream = DataStream.readFromStream(inputStream);
			Decoder decoder = Decoder.create(dataStream);
			assertNotNull(decoder);
		}
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("Elapsed time " + elapsed + "ms");
		System.out.println("Average time " + (elapsed / count) + "ms");

		System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
	}
}