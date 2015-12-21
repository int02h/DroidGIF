package com.dpforge.droidgif.datastream;

import java.io.IOException;
import java.io.InputStream;

class GraphicControlExtension extends ExtensionBlock {
	final static int DISPOSAL_NOT_SPECIFIED = 0;
	final static int DISPOSAL_NO = 1;
	final static int DISPOSAL_RESTORE_BACKGROUND = 2;
	final static int DISPOSAL_RESTORE_PREVIOUS = 3;

	private int mDisposalMethod;
	private boolean mUserInput;
	private boolean mTransparentColor;
	private int mDelay;
	private int mTransparencyColorIndex;

	@Override
	void read(final InputStream is) throws IOException, InvalidDataStreamException {
		final int size = BinaryUtils.readByte(is);
		if (size != 4) {
			throw new InvalidDataStreamException(
					InvalidDataStreamException.ERROR_WRONG_VALUE,
					"Graphic Control extension's block size must be 4 bytes. But was " + size + "."
			);
		}

		final int flag = BinaryUtils.readByte(is);
		mDisposalMethod = ((flag & 0x1C) >> 2);
		mUserInput = ((flag & 0x02) == 0x02);
		mTransparentColor = ((flag & 0x02) == 0x02);

		mDelay = BinaryUtils.readInt16(is);
		mTransparencyColorIndex = BinaryUtils.readByte(is);

		int terminator = BinaryUtils.readByte(is);
		if (terminator != 0) {
			throw new InvalidDataStreamException(
					InvalidDataStreamException.ERROR_TERMINATOR_NOT_ZERO,
					"Graphic Control terminator"
			);
		}
	}

}
