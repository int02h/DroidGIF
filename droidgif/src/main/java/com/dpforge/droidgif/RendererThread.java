package com.dpforge.droidgif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder2.GIFImage;
import com.dpforge.droidgif.decoder2.GIFImageFrame;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private volatile boolean mRunning;
	private GIFImage mImage;
	private int[] mBuffer;
	private Bitmap mFrameBitmap;
	private final Paint mPaint;

	private long mTotalDiff;
	private int mFrameIndex;

	RendererThread(final SurfaceHolder holder) {
		mHolder = holder;

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void setImage(final GIFImage image) {
		synchronized (RendererThread.this) {
			mImage = image;
			mBuffer = new int[image.height()*image.width()];
			mFrameBitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
			mFrameIndex = 0;
			mTotalDiff = 0;
		}
	}

	@Override
	public void run() {
		long lastDrawTime = System.currentTimeMillis();
		while (mRunning) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				synchronized (RendererThread.this) {
					if (canvas != null && mImage != null) {
						final GIFImageFrame frame = mImage.getFrame(mFrameIndex);
						prepareBuffer(frame);
						drawBuffer(canvas);
						disposeBuffer(frame);
						mTotalDiff += System.currentTimeMillis() - lastDrawTime;
						lastDrawTime = System.currentTimeMillis();

						if (mTotalDiff >= frame.delay()*10) {
							mFrameIndex = (mFrameIndex + 1)%mImage.framesCount();
							mTotalDiff = 0;
						}
					}
				}
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private void prepareBuffer(final GIFImageFrame frame) {
		for (int y = 0; y < frame.height(); ++y) {
			for (int x = 0; x < frame.width(); ++x) {
				int left = frame.left() + x;
				int top = frame.top() + y;
				if (!frame.isTransparentPixel(x, y)) {
					mBuffer[top*mImage.width() + left] = frame.getColor(x, y) | 0xFF000000;
				}
			}
		}
	}

	private void drawBuffer(final Canvas canvas) {
		mFrameBitmap.setPixels(mBuffer, 0, mImage.width(), 0, 0, mImage.width(), mImage.height());
		canvas.drawBitmap(mFrameBitmap, 0, 0, mPaint);
	}

	private void disposeBuffer(final GIFImageFrame frame) {
		switch (frame.disposalMethod()) {
			case NOT_SPECIFIED:
			case NO_DISPOSE:
				// DO NOTHING
				break;
			case RESTORE_BACKGROUND:
				for (int i = 0; i < mBuffer.length; ++i) {
					mBuffer[i] = mImage.backgroundColor();
				}
				break;
			case RESTORE_PREVIOUS:
				// TODO: implement
				break;
		}
	}
}
