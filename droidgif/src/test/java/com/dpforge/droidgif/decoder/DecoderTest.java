package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.DataStream;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class DecoderTest {
	@Test
	public void testCreate() throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
		DataStream dataStream = DataStream.readFromStream(inputStream);
		Decoder decoder = Decoder.create(dataStream);
		assertNotNull(decoder);
	}
}