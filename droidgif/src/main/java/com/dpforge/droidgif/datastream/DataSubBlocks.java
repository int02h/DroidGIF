package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class DataSubBlocks extends DataStreamBlock {
	private byte[] mData;

	@Override
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		int size;
		int totalSize = 0;
		final List<byte[]> dataList = new ArrayList<>();

		while ((size = BinaryUtils.readByte(is)) > 0) {
			byte[] data = BinaryUtils.readBytes(is, size);
			dataList.add(data);
			totalSize += size;
		}

		mData = new byte[totalSize];
		int index = 0;
		for (byte[] data : dataList) {
			System.arraycopy(data, 0, mData, index, data.length);
			index += data.length;
		}
	}
}
