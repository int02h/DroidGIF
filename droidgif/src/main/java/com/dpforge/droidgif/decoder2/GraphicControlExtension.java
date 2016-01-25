package com.dpforge.droidgif.decoder2;

import java.io.IOException;

class GraphicControlExtension extends DecoderReader {
	final static int DISPOSAL_NOT_SPECIFIED = 0;
	final static int DISPOSAL_NO = 1;
	final static int DISPOSAL_RESTORE_BACKGROUND = 2;
	final static int DISPOSAL_RESTORE_PREVIOUS = 3;

	private int mDisposalMethod;
	private boolean mUserInput;
	private boolean mTransparentColor;
	private int mDelay;
	private int mTransparencyColorIndex;

	GraphicControlExtension() {
	}

	public int disposalMethod() {
		return mDisposalMethod;
	}

	public boolean hasUserInput() {
		return mUserInput;
	}

	public boolean hasTransparentColor() {
		return mTransparentColor;
	}

	public int transparencyColorIndex() {
		return mTransparencyColorIndex;
	}

	public int delay() {
		return mDelay;
	}

	@Override
	void read(final BinaryStream stream) throws IOException, DecoderException {
		final int size = stream.readByte();
		if (size != 4) {
			throw new DecoderException(
					DecoderException.ERROR_WRONG_VALUE,
					"Graphic Control extension's block size must be 4 bytes. But was " + size + "."
			);
		}

		final int flag = stream.readByte();
		mDisposalMethod = ((flag & 0x1C) >> 2);
		mUserInput = ((flag & 0x02) == 0x02);
		mTransparentColor = ((flag & 0x02) == 0x02);

		mDelay = stream.readInt16();
		mTransparencyColorIndex = stream.readByte();

		int terminator = stream.readByte();
		if (terminator != 0) {
			throw new DecoderException(
					DecoderException.ERROR_TERMINATOR_NOT_ZERO,
					"Graphic Control terminator"
			);
		}
	}
}
