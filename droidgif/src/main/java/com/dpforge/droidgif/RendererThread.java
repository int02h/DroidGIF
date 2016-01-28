package com.dpforge.droidgif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder2.GIFImage;
import com.dpforge.droidgif.decoder2.GIFImageFrame;

import java.util.Arrays;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private volatile boolean mRunning;
	private volatile State mState = State.INIT;
	private final Object mMonitor = new Object();

	private GIFImage mImage;
	private int[] mBuffer;
	private Bitmap mFrameBitmap;
	private int mFrameIndex;
	private long mTotalDiff;

	RendererThread(final SurfaceHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void setImage(final GIFImage image) {
		synchronized (RendererThread.this) {
			mImage = image;
			mBuffer = new int[image.height()*image.width()];
			mFrameBitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
			mState = State.INIT;
		}
		waitFrameRendered();
	}

	void startRendering() {
		synchronized (RendererThread.this) {
			if (mState == State.INIT || mState == State.STOPPED) {
				mFrameIndex = 0;
				mTotalDiff = 0;
				mState = State.RENDERING;
			}
		}
		waitFrameRendered();
	}

	void stopRendering() {
		synchronized (RendererThread.this) {
			if (mState == State.PAUSED || mState == State.RENDERING) {
				mFrameIndex = 0;
				mTotalDiff = 0;
				mState = State.STOPPED;
			}
		}
		waitFrameRendered();
	}

	void pauseRendering() {
		synchronized (RendererThread.this) {
			if (mState == State.RENDERING) {
				mState = State.PAUSED;
			}
		}
		waitFrameRendered();
	}

	void resumeRendering() {
		synchronized (RendererThread.this) {
			if (mState == State.PAUSED) {
				mState = State.RENDERING;
			}
		}
		waitFrameRendered();
	}

	@Override
	public void run() {
		long lastDrawTime = System.currentTimeMillis();
		while (mRunning) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				synchronized (RendererThread.this) {
					switch (mState) {
						case RENDERING:
							if (canvas != null) {
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
							break;
						case PAUSED:
							if (canvas != null) {
								final GIFImageFrame frame = mImage.getFrame(mFrameIndex);
								prepareBuffer(frame);
								drawBuffer(canvas);
								disposeBuffer(frame);
							}
							break;
						case STOPPED:
						case INIT:
							if (canvas != null) {
								Arrays.fill(mBuffer, 0xFF000000);
								drawBuffer(canvas);
							}
							break;
					}
				}
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}

			synchronized (mMonitor) {
				mMonitor.notifyAll();
			}
		}
	}

	private void waitFrameRendered() {
		synchronized (mMonitor) {
			if (mRunning) {
				try {
					mMonitor.wait(1000);
				} catch (InterruptedException ignored) {
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
		canvas.drawBitmap(mFrameBitmap, 0, 0, null);
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

	private enum State {
		INIT,
		RENDERING,
		PAUSED,
		STOPPED
	}
}
