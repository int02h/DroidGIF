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
	private final List<GraphicBlock> mGraphicBlocks = new ArrayList<>();

	private Decoder() {
	}

	public static Decoder create(final DataStream stream) {
		final Decoder decoder = new Decoder();
		decoder.read(stream);
		return decoder;
	}

	private void read(final DataStream stream) {
		GraphicBlock graphicBlock = null;

		for (DataStreamBlock block : stream.blockList()) {
			if (!isSpecialPurposeBlock(block)) {
				if (graphicBlock == null) {
					graphicBlock = new GraphicBlock();
				}

				if (block instanceof GraphicControlExtension) {
					graphicBlock.setExtension((GraphicControlExtension) block);
				} else if (block instanceof TableBasedImage) {
					graphicBlock.setImage((TableBasedImage) block);
					mGraphicBlocks.add(graphicBlock);
					graphicBlock = null;
				}
			}
		}
	}

	private static boolean isSpecialPurposeBlock(DataStreamBlock block) {
		return (block instanceof ApplicationExtension)
				|| (block instanceof CommentExtension);
	}
}
