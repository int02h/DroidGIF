package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.ApplicationExtension;
import com.dpforge.droidgif.datastream.CommentExtension;
import com.dpforge.droidgif.datastream.DataStream;
import com.dpforge.droidgif.datastream.DataStreamBlock;
import com.dpforge.droidgif.datastream.GraphicControlExtension;
import com.dpforge.droidgif.datastream.TableBasedImage;

import java.util.ArrayList;
import java.util.List;

public class Decoder {
	private final List<ImageGraphicBlock> mGraphicBlocks = new ArrayList<>();

	private Decoder() {
	}

	public static Decoder create(final DataStream stream) {
		final Decoder decoder = new Decoder();
		decoder.read(stream);
		return decoder;
	}

	private void read(final DataStream stream) {
		GraphicControlExtension graphicExtension = null;
		for (DataStreamBlock block : stream.blockList()) {
			if (!isSpecialPurposeBlock(block)) {
				if (block instanceof GraphicControlExtension) {
					graphicExtension = (GraphicControlExtension) block;
				} else if (block instanceof TableBasedImage) {
					ImageGraphicBlock image = new ImageGraphicBlock((TableBasedImage) block);
					image.setExtension(graphicExtension);
					if (stream.logicalScreen().hasGlobalColorTable()) {
						image.setGlobalColorTable(stream.logicalScreen().globalColorTable());
					}
					image.decompress();
					mGraphicBlocks.add(image);
				}
			}
		}
	}

	private static boolean isSpecialPurposeBlock(DataStreamBlock block) {
		return (block instanceof ApplicationExtension)
				|| (block instanceof CommentExtension);
	}
}
