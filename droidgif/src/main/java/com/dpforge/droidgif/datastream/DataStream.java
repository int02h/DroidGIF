package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

public class DataStream {
	private final Header mHeader = new Header();
	private final LogicalScreen mLogicalScreen = new LogicalScreen();

	private DataStream() {
	}

	public static DataStream readFromStream(final InputStream is) throws IOException, InvalidDataStreamException {
		final DataStream dataStream = new DataStream();
		dataStream.read(is);
		return dataStream;
	}

	private void read(final InputStream is) throws IOException, InvalidDataStreamException {
		mHeader.read(is);
		mLogicalScreen.read(is);
	}
}
