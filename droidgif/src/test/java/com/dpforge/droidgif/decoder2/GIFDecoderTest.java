package com.dpforge.droidgif.decoder2;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by d.popov on 22/01/16.
 */
public class GIFDecoderTest {

	@Test
	public void testCreate() throws Exception {
		final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
		final GIFDecoder decoder = new GIFDecoder(inputStream);
		decoder.decode();
	}
}