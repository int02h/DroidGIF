package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.DataStream;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class DecoderTest {
	@Test
	public void testCreate() throws Exception {
		long startTime = System.currentTimeMillis();
		int count = 10000;
		for (int i = 0; i < count; ++i) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
			DataStream dataStream = DataStream.readFromStream(inputStream);
			Decoder decoder = Decoder.create(dataStream);
			assertNotNull(decoder);
		}
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("[old] Elapsed time " + elapsed + "ms");
		System.out.println("[old] Average time " + (elapsed / count) + "ms");
	}
}