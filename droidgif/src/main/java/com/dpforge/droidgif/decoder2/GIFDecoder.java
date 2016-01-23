package com.dpforge.droidgif.decoder2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GIFDecoder {
	private final static String GIF_SIGNATURE = "GIF";
	private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Arrays.asList("87a", "89a"));
	private final static int GIF_TRAILER = 0x3B;
	private final static int EXTENSION_LABEL = 0x21;
	private final static int EXTENSION_APPLICATION = 0xFF;
	private final static int EXTENSION_GRAPHIC_CONTROL = 0xF9;
	private final static int TABLE_BASED_IMAGE_LABEL = 0x2C;

	private final BinaryStream mStream;
	private final GIFImage mImage = new GIFImage();

	public GIFDecoder(final InputStream inputStream) {
		mStream = new BinaryStream(inputStream);
	}

	void decode() throws IOException, DecoderException {
		readHeader();
		readLogicalScreen();
		readData();
	}

	private void readHeader() throws IOException, DecoderException {
		final String signature = mStream.readASCIIString(3);
		if (!GIF_SIGNATURE.equals(signature))
			throw new DecoderException(DecoderException.ERROR_WRONG_SIGNATURE);

		final String version = mStream.readASCIIString(3);
		if (!SUPPORTED_VERSIONS.contains(version))
			throw new DecoderException(DecoderException.ERROR_UNSUPPORTED_VERSION);
	}

	private void readLogicalScreen() throws IOException, DecoderException {
		final LogicalScreenDecoder decoder = new LogicalScreenDecoder();
		decoder.read(mStream);

		if (decoder.hasGlobalColorTable()) {
			mImage.setGlobalColorTable(decoder.globalColorTable());
		}
	}

	private void readData() throws IOException, DecoderException {
		int label;
		while ((label = mStream.readByte()) != GIF_TRAILER) {
			switch (label) {
				case EXTENSION_LABEL:
					label = mStream.readByte();
					switch (label) {
						case EXTENSION_APPLICATION:
							new ApplicationExtensionDecoder().read(mStream);
							break;
						case EXTENSION_GRAPHIC_CONTROL:
							new GraphicControlExtensionDecoder().read(mStream);
							break;
						default:
							throw new DecoderException(
									DecoderException.ERROR_UNSUPPORTED_EXTENSION,
									"Extension label " + label
							);
					}
					break;
				case TABLE_BASED_IMAGE_LABEL:
					new TableBasedImageDecoder(mImage.getGlobalColorTable()).read(mStream);
					break;
			}
		}
	}

}
