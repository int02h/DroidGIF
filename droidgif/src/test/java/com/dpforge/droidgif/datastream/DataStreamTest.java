package com.dpforge.droidgif.datastream;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class DataStreamTest {
	@Test
	public void testReadFromStream() throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("earthquake.gif");
		DataStream dataStream = DataStream.readFromStream(inputStream);
		assertNotNull(dataStream);
	}
}