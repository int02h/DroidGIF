package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

public abstract class DataStreamBlock {
	protected DataStreamBlock() {
	}

	abstract void read(final InputStream is) throws IOException, InvalidDataStreamException;
}
