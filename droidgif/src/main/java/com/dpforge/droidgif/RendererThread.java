package com.dpforge.droidgif;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder.Decoder;
import com.dpforge.droidgif.decoder.ImageGraphicBlock;
import com.dpforge.droidgif.decoder2.GIFImage;
import com.dpforge.droidgif.decoder2.GIFImageFrame;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private boolean mRunning;
	private GIFImage mImage;

	RendererThread(final SurfaceHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void setImage(final GIFImage image) {
		mImage = image;
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
		if (mImage.isEmpty())
			return;

		final GIFImageFrame frame = mImage.getFrame(0);
		final Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		for (int y = 0; y < frame.height(); ++y) {
			for (int x = 0; x < frame.width(); ++x) {
				final int color = frame.getColor(x, y);
				paint.setColor(color | 0xFF000000);
				canvas.drawPoint(x, y, paint);
			}
		}
	}
}
