package com.dpforge.droidgif;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private boolean mRunning;

	RendererThread(final SurfaceHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
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

	}
}
