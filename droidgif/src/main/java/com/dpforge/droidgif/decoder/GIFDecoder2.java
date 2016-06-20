package com.dpforge.droidgif.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GIFDecoder2 {
	private final static String GIF_SIGNATURE = "GIF";
	private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Arrays.asList("87a", "89a"));
	private final static int GIF_TRAILER = 0x3B;
	private final static int EXTENSION_LABEL = 0x21;
	private final static int EXTENSION_APPLICATION = 0xFF;
	private final static int EXTENSION_GRAPHIC_CONTROL = 0xF9;
	private final static int EXTENSION_COMMENT = 0xFE;
	private final static int IMAGE_DESCRIPTOR_LABEL = 0x2C;

	private GIFDecoder2() {
	}

	public static GIFImage decode(final InputStream is) throws IOException, DecoderException {
		final GIFImage image = new GIFImage();
		final BinaryStream bs = new BinaryStream(is);
		checkHeader(bs);
		readLogicalScreen(bs, image);
		readData(bs, image);
		image.finishDecoding();
		return image;
	}

	private static void checkHeader(final BinaryStream stream) throws IOException, DecoderException {
		final String signature = stream.readASCIIString(3);
		if (!GIF_SIGNATURE.equals(signature))
			throw new DecoderException(DecoderException.ERROR_WRONG_SIGNATURE);

		final String version = stream.readASCIIString(3);
		if (!SUPPORTED_VERSIONS.contains(version))
			throw new DecoderException(DecoderException.ERROR_UNSUPPORTED_VERSION);
	}

	private static void readLogicalScreen(final BinaryStream stream, final GIFImage image) throws IOException, DecoderException {
		final int width = stream.readInt16();
		final int height = stream.readInt16();
		image.setSize(width, height);

		final int flag = stream.readByte();
		final boolean hasGlobalColorTable = ((flag & 0x80) == 0x80);
		// noinspection unused
		final int colorResolution = ((flag & 0x70) >> 4);
		// noinspection unused
		final boolean isGlobalTableSorted = ((flag & 0x08) == 0x08);
		final int globalColorTableSize = (flag & 0x07);

		final int backgroundColorIndex = stream.readByte();
		// noinspection unused
		final int pixelAspectRatio = stream.readByte();
		image.setBackgroundColorIndex(backgroundColorIndex);

		if (hasGlobalColorTable) {
			int size = (int) Math.pow(2, globalColorTableSize + 1);
			final ColorTable globalColorTable = new ColorTable(size);
			globalColorTable.read(stream);
			image.setGlobalColorTable(globalColorTable);
		}
	}

	private static void readData(final BinaryStream stream, final GIFImage image) throws IOException, DecoderException {
		int label;
		GIFImageFrame frame = null;
		while ((label = stream.readByte()) != GIF_TRAILER) {
			switch (label) {
				case EXTENSION_LABEL:
					label = stream.readByte();
					switch (label) {
						case EXTENSION_APPLICATION:
							readApplicationExtension(stream);
							break;
						case EXTENSION_COMMENT:
							readCommentExtension(stream);
							break;
						case EXTENSION_GRAPHIC_CONTROL:
							if (frame == null) frame = new GIFImageFrame();
							readGraphicControlExtension(stream, frame);
							break;
						default:
							throw new DecoderException(
									DecoderException.ERROR_UNSUPPORTED_EXTENSION,
									"Extension label " + label
							);
					}
					break;
				case IMAGE_DESCRIPTOR_LABEL:
					if (frame == null) frame = new GIFImageFrame();
					readRenderingBlock(stream, image, frame);
					frame = null;
					break;
			}
		}
	}

	private static void readApplicationExtension(final BinaryStream stream) throws IOException, DecoderException {
		final int size = stream.readByte();
		if (size != 11) {
			throw new DecoderException(
					DecoderException.ERROR_WRONG_VALUE,
					"Application extension's block size must be 11 bytes. But was " + size + "."
			);
		}

		// noinspection unused
		final String applicationId = stream.readASCIIString(8);
		// noinspection unused
		final String applicationCode = stream.readASCIIString(3);
		skipSubBlocks(stream);
	}

	private static void readCommentExtension(final BinaryStream stream) throws IOException {
		skipSubBlocks(stream);
	}

	private static void readGraphicControlExtension(final BinaryStream stream, final GIFImageFrame frame)
			throws IOException, DecoderException {
		final int size = stream.readByte();
		if (size != 4) {
			throw new DecoderException(
					DecoderException.ERROR_WRONG_VALUE,
					"Graphic Control extension's block size must be 4 bytes. But was " + size + "."
			);
		}

		final int flag = stream.readByte();
		frame.setDisposalMethod(DisposalMethod.fromInt((flag & 0x1C) >> 2));
		// noinspection unused
		final boolean userInput = ((flag & 0x02) == 0x02);
		final boolean hasTransparentColor = ((flag & 0x01) == 0x01);

		final int delay = stream.readInt16();
		final int transparencyColorIndex = stream.readByte();

		frame.setTransparentColorIndex(hasTransparentColor ? transparencyColorIndex : -1);
		frame.setDelay(delay);

		int terminator = stream.readByte();
		if (terminator != 0) {
			throw new DecoderException(
					DecoderException.ERROR_TERMINATOR_NOT_ZERO,
					"Graphic Control terminator"
			);
		}
	}

	private static void readRenderingBlock(final BinaryStream stream, final GIFImage image,
										   final GIFImageFrame frame) throws IOException, DecoderException {
		final int left = stream.readInt16();
		final int top = stream.readInt16();
		frame.setPosition(left, top);

		final int width = stream.readInt16();
		final int height = stream.readInt16();
		frame.setSize(width, height);

		final int flag = stream.readByte();
		final boolean hasLocalColorTable = ((flag & 0x80) == 0x80);
		// noinspection unused
		final boolean interlace = ((flag & 0x40) == 0x40);
		// noinspection unused
		final boolean isLocalTableSorted = ((flag & 0x20) == 0x20);
		final int localColorTableSize = (flag & 0x07);

		if (hasLocalColorTable) {
			int size = (int) Math.pow(2, localColorTableSize + 1);
			final ColorTable localColorTable = new ColorTable(size);
			localColorTable.read(stream);
			frame.setColorTable(localColorTable);
		} else {
			frame.setColorTable(image.globalColorTable());
		}

		final int minCodeSize = stream.readByte();
		final SubBlocksInputStream compressedData = new SubBlocksInputStream(stream, frame.width()*frame.height());
		compressedData.prepare();
		frame.setCompressedData(minCodeSize, compressedData);

		image.addFrame(frame);
	}

	private static void skipSubBlocks(final BinaryStream stream) throws IOException {
		int size;
		while ((size = stream.readByte()) > 0) {
			stream.skipBytes(size);
		}
	}
}
