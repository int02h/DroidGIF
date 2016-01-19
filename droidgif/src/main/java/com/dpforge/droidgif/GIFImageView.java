package com.dpforge.droidgif;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GIFImageView extends SurfaceView {
	private RendererThread mRendererThread;

	public GIFImageView(final Context context) {
		super(context);
		getHolder().addCallback(new SurfaceHolderCallback());
	}

	private class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			mRendererThread = new RendererThread(getHolder());
			mRendererThread.setRunning(true);
			mRendererThread.start();
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {

		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			boolean retry = true;
			mRendererThread.setRunning(false);
			while (retry) {
				try {
					mRendererThread.join();
					retry = false;
				} catch (InterruptedException ignored) {

				}
			}
		}
	}
}
