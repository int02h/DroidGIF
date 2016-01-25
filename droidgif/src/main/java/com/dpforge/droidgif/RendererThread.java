package com.dpforge.droidgif;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder2.GIFImage;
import com.dpforge.droidgif.decoder2.GIFImageFrame;

class RendererThread extends Thread {
	private final SurfaceHolder mHolder;
	private boolean mRunning;
	private GIFImage mImage;
	private int[][] mBuffer;
	private final Paint mPaint;

	private long mTotalDiff;
	private long mLastDrawTime;
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
		mImage = image;
		mBuffer = new int[image.height()][image.width()];
	}

	@Override
	public void run() {
		mLastDrawTime = System.currentTimeMillis();
		while (mRunning) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				if (canvas != null) {
					final GIFImageFrame frame = mImage.getFrame(mFrameIndex);
					prepareBuffer(frame);
					drawBuffer(canvas);
					disposeBuffer(frame);
					mTotalDiff += System.currentTimeMillis() - mLastDrawTime;
					mLastDrawTime = System.currentTimeMillis();

					if (mTotalDiff > frame.delay()*10) {
						mFrameIndex = (mFrameIndex + 1)%mImage.framesCount();
						mTotalDiff = 0;
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
			int[] row = mBuffer[frame.top() + y];
			for (int x = 0; x < frame.width(); ++x) {
				row[frame.left() + x] = frame.getColor(x, y);
			}
		}
	}

	private void drawBuffer(final Canvas canvas) {
		for (int y = 0; y < mBuffer.length; ++y) {
			for (int x = 0; x < mBuffer[0].length; ++x) {
				final int color = mBuffer[y][x];
				mPaint.setColor(color | 0xFF000000);
				canvas.drawPoint(x, y, mPaint);
			}
		}
	}

	private void disposeBuffer(final GIFImageFrame frame) {
		switch (frame.disposalMethod()) {
			case NOT_SPECIFIED:
			case NO_DISPOSE:
				// DO NOTHING
				break;
			case RESTORE_BACKGROUND:
				for (int y = 0; y < mBuffer.length; ++y) {
					for (int x = 0; x < mBuffer[0].length; ++x) {
						mBuffer[y][x] = mImage.backgroundColor();
					}
				}
				break;
			case RESTORE_PREVIOUS:
				// TODO: implement
				break;
		}
	}
}
