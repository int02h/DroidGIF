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

	public GIFDecoder(final InputStream inputStream) {
		mStream = new BinaryStream(inputStream);
	}

	public GIFImage decode() throws IOException, DecoderException {
		final GIFImage image = new GIFImage();
		readHeader();
		readLogicalScreen(image);
		readData(image);
		return image;
	}

	private void readHeader() throws IOException, DecoderException {
		final String signature = mStream.readASCIIString(3);
		if (!GIF_SIGNATURE.equals(signature))
			throw new DecoderException(DecoderException.ERROR_WRONG_SIGNATURE);

		final String version = mStream.readASCIIString(3);
		if (!SUPPORTED_VERSIONS.contains(version))
			throw new DecoderException(DecoderException.ERROR_UNSUPPORTED_VERSION);
	}

	private void readLogicalScreen(final GIFImage image) throws IOException, DecoderException {
		final LogicalScreen decoder = new LogicalScreen();
		decoder.read(mStream);

		if (decoder.hasGlobalColorTable()) {
			image.setGlobalColorTable(decoder.globalColorTable());
		}
	}

	private void readData(final GIFImage image) throws IOException, DecoderException {
		int label;
		GraphicControlExtension graphicControlDecoder = null;
		while ((label = mStream.readByte()) != GIF_TRAILER) {
			switch (label) {
				case EXTENSION_LABEL:
					label = mStream.readByte();
					switch (label) {
						case EXTENSION_APPLICATION:
							new ApplicationExtension().read(mStream);
							break;
						case EXTENSION_GRAPHIC_CONTROL:
							graphicControlDecoder = new GraphicControlExtension();
							graphicControlDecoder.read(mStream);
							break;
						default:
							throw new DecoderException(
									DecoderException.ERROR_UNSUPPORTED_EXTENSION,
									"Extension label " + label
							);
					}
					break;
				case TABLE_BASED_IMAGE_LABEL:
					final TableBasedImage imageDecoder = new TableBasedImage(image.globalColorTable());
					imageDecoder.read(mStream);
					image.addFrame(imageDecoder, graphicControlDecoder);
					graphicControlDecoder = null;
					break;
			}
		}
	}

}
