package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.datastream.GraphicControlExtension;
import com.dpforge.droidgif.datastream.TableBasedImage;

public class GraphicBlock {
	private GraphicControlExtension mExtension;
	private TableBasedImage mImage;

	void setExtension(final GraphicControlExtension extension) {
		mExtension = extension;
	}

	void setImage(final TableBasedImage image) {
		mImage = image;
	}
}
