package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DataStream {
	private final Header mHeader = new Header();
	private final LogicalScreen mLogicalScreen = new LogicalScreen();
	private final List<DataStreamEntity> mData = new ArrayList<>();

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

		int label;
		while ((label = BinaryUtils.readByte(is)) != 0x3B) {
			final DataStreamEntity entity;
			switch (label) {
				case ExtensionBlock.LABEL:
					entity = ExtensionBlock.readBlock(is);
					break;
				case TableBasedImage.LABEL:
					entity = new TableBasedImage();
					entity.read(is);
					break;
				default:
					throw new InvalidDataStreamException(
							InvalidDataStreamException.ERROR_UNSUPPORTED_BLOCK,
							"Block label " + label
					);
			}
			mData.add(entity);
		}
	}
}
