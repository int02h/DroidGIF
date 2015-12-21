package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

abstract class DataStreamEntity {
	abstract void read(final InputStream is) throws IOException, InvalidDataStreamException;
}
