package com.dpforge.droidgif.decoder2;

import com.dpforge.droidgif.decoder2.lzw.LZW;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class TableBasedImageDecoder extends BaseDecoder {
	private int mLeft;
	private int mTop;
	private int mWidth;
	private int mHeight;
	private boolean mHasLocalColorTable;
	private boolean mInterlace;
	private boolean mIsLocalTableSorted;

	private ColorTable mLocalColorTable;
	private final ColorTable mGlobalColorTable;

	TableBasedImageDecoder(final ColorTable globalColorTable) {
		mGlobalColorTable = globalColorTable;
	}

	public int left() {
		return mLeft;
	}

	public int top() {
		return mTop;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public boolean hasLocalColorTable() {
		return mHasLocalColorTable;
	}

	public boolean isLocalTableSorted() {
		return mIsLocalTableSorted;
	}

	public ColorTable localColorTable() {
		return mLocalColorTable;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		mLeft = stream.readInt16();
		mTop = stream.readInt16();
		mWidth = stream.readInt16();
		mHeight = stream.readInt16();

		final int flag = stream.readByte();
		mHasLocalColorTable = ((flag & 0x80) == 0x80);
		mInterlace = ((flag & 0x40) == 0x40);
		mIsLocalTableSorted = ((flag & 0x20) == 0x20);
		final int localColorTableSize = (flag & 0x07);

		if (mHasLocalColorTable) {
			int size = (int) Math.pow(2, localColorTableSize + 1);
			ColorTableDecoder colorTableDecoder = new ColorTableDecoder(size);
			colorTableDecoder.read(stream);
			mLocalColorTable = colorTableDecoder.colorTable();
		}

		readImageData(stream);
	}

	private void readImageData(final BinaryStream stream) throws IOException, DecoderException {
		final int minCodeSize = stream.readByte();
		final SubBlocksInputStream subStream = new SubBlocksInputStream(stream);
		final List<Integer> colorIndices = LZW.decompress(new InputStream() {
			@Override
			public int read() throws IOException {
				return subStream.read();
			}
		}, minCodeSize + 1, colorTable().size());

		if (subStream.read() != -1) { // read last zero byte
			throw new DecoderException(
					DecoderException.ERROR_WRONG_IMAGE_DATA,
					"Redundant bytes in image data"
			);
		}

		if (colorIndices.size() != mWidth*mHeight)
			throw new DecoderException(
					DecoderException.ERROR_WRONG_IMAGE_DATA,
					"Wrong size of color indices list"
			);
	}

	private ColorTable colorTable() {
		return mHasLocalColorTable ? mLocalColorTable : mGlobalColorTable;
	}
}
