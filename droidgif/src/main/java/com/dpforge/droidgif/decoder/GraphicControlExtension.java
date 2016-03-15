package com.dpforge.droidgif.decoder;

import java.io.IOException;

class GraphicControlExtension extends DecoderReader {
	private DisposalMethod mDisposalMethod;
	private boolean mUserInput;
	private boolean mTransparentColor;
	private int mDelay;
	private int mTransparencyColorIndex;

	GraphicControlExtension() {
	}

	public DisposalMethod disposalMethod() {
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
		mDisposalMethod = DisposalMethod.fromInt((flag & 0x1C) >> 2);
		mUserInput = ((flag & 0x02) == 0x02);
		mTransparentColor = ((flag & 0x01) == 0x01);

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
