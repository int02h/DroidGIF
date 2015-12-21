package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

abstract class DataStreamBlock {
	abstract void read(final InputStream is) throws IOException, InvalidDataStreamException;
}
