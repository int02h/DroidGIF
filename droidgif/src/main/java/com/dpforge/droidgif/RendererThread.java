package com.dpforge.droidgif;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder.Decoder;
import com.dpforge.droidgif.decoder.ImageGraphicBlock;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private boolean mRunning;
	private Decoder mDecoder;

	RendererThread(final SurfaceHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void setDecoder(final Decoder decoder) {
		mDecoder = decoder;
	}

	@Override
	public void run() {
		while (mRunning) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				if (canvas != null) {
					drawFrame(canvas);
				}
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private void drawFrame(final Canvas canvas) {
		if (mDecoder.graphicBlockCount() == 0)
			return;

		final ImageGraphicBlock image = mDecoder.getGraphicBlock(0);
		final Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		for (int y = 0; y < image.height(); ++y) {
			for (int x = 0; x < image.width(); ++x) {
				final int color = image.getColor(x, y);
				paint.setColor(color | 0xFF000000);
				canvas.drawPoint(x, y, paint);
			}
		}
	}
}
