package com.dpforge.droidgif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.dpforge.droidgif.decoder.GIFImage;
import com.dpforge.droidgif.decoder.GIFImageFrame;

import java.util.ArrayList;
import java.util.List;

class RenderThread extends Thread {
	private final CanvasHolder mHolder;
	private volatile boolean mRunning;

	private RenderState mState = RenderState.UNKNOWN;
	private final List<RenderStateChange> mStateChanges = new ArrayList<>();
	private final List<RenderOptionsChange> mOptionsChanges = new ArrayList<>();

	RenderThread(final CanvasHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void executeCommand(final Command command) {
		executeCommand(command, null);
	}

	void executeCommand(final Command command, final GIFImage image) {
		if (!canExecuteCommand(command, mState))
			return;

		RenderStateChange change = new RenderStateChange();
		switch (command) {
			case SET_IMAGE:
				change.setImage(image);
				change.setFrameIndex(0);
				change.setState(mState = RenderState.INIT);
				break;
			case PLAY:
				if (mState != RenderState.PAUSED) {
					change.setFrameIndex(0);
				}
				change.setState(mState = RenderState.RENDERING);
				break;
			case STOP:
				change.setFrameIndex(0);
				change.setState(mState = RenderState.STOPPED);
				break;
			case PAUSE:
				change.setState(mState = RenderState.PAUSED);
				break;
		}

		synchronized (mStateChanges) {
			mStateChanges.add(change);
		}
	}

	void setViewSize(int width, int height) {
		synchronized (mOptionsChanges) {
			mOptionsChanges.add(new RenderOptionsChange().setViewSize(width, height));
		}
	}

	void setBackgroundColor(final int color) {
		synchronized (mOptionsChanges) {
			mOptionsChanges.add(new RenderOptionsChange().setBackgroundColor(color | 0xFF000000));
		}
	}

	@Override
	public void run() {
		final RenderContext context = new RenderContext();
		context.lastDrawTime = System.currentTimeMillis();
		context.state = RenderState.UNKNOWN;
		context.frameIndex = 0;
		context.totalDiff = 0;
		context.options = new RenderOptions();

		while (mRunning) {
			synchronized (mOptionsChanges) {
				for (final RenderOptionsChange change : mOptionsChanges) {
					change.apply(context.options);
					if (change.isSizeModified() || change.isGravityModified()) {
						updateDrawMatrix(context);
					}
				}
				mOptionsChanges.clear();
			}

			synchronized (mStateChanges) {
				for (final RenderStateChange change : mStateChanges) {
					applyChange(change, context);
				}
				mStateChanges.clear();
			}

			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				draw(canvas, context);
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private static void applyChange(final RenderStateChange change, final RenderContext context) {
		if (change.hasModifications()) {
			if (change.isImageModified()) {
				context.image = change.getImage();
				context.buffer = new int[context.image.height()*context.image.width()];
				context.frameBitmap = Bitmap.createBitmap(context.image.width(),
						context.image.height(), Bitmap.Config.ARGB_8888);
				updateDrawMatrix(context);
			}
			if (change.isFrameIndexModified()) {
				context.frameIndex = change.getFrameIndex();
				context.totalDiff = 0;
			}
			if (change.isStateModified()) {
				context.state = change.getState();
			}
		}
	}

	private static boolean canExecuteCommand(final Command command, final RenderState state) {
		switch (command) {
			case SET_IMAGE:
				return true;
			case PLAY:
				return (state == RenderState.INIT || state == RenderState.STOPPED || state == RenderState.PAUSED);
			case STOP:
				return (state == RenderState.PAUSED || state == RenderState.RENDERING);
			case PAUSE:
				return (state == RenderState.RENDERING);
			default:
				return false;
		}
	}

	private static void draw(final Canvas canvas, final RenderContext context) {
		if (canvas == null)
			return;
		canvas.drawColor(context.options.backgroundColor);
		switch (context.state) {
			case RENDERING:
				if (context.image != null) {
					final GIFImageFrame frame = context.image.frameDecoder().getFrame(context.frameIndex);
					prepareBuffer(frame, context);
					drawBuffer(canvas, context);
					disposeBuffer(frame, context);

					context.totalDiff += System.currentTimeMillis() - context.lastDrawTime;
					context.lastDrawTime = System.currentTimeMillis();

					if (context.totalDiff >= frame.delay()*10) {
						context.frameIndex = (context.frameIndex + 1)%context.image.framesCount();
						context.totalDiff = 0;
					}
				}
				break;
			case PAUSED:
				if (context.image != null) {
					final GIFImageFrame frame = context.image.frameDecoder().getFrame(context.frameIndex);
					prepareBuffer(frame, context);
					drawBuffer(canvas, context);
					disposeBuffer(frame, context);
				}
				break;
			case STOPPED:
			case INIT:
				break;
		}
	}

	private static void prepareBuffer(final GIFImageFrame frame, final RenderContext context) {
		for (int y = 0; y < frame.height(); ++y) {
			for (int x = 0; x < frame.width(); ++x) {
				int left = frame.left() + x;
				int top = frame.top() + y;
				if (!frame.isTransparentPixel(x, y)) {
					context.buffer[top*context.image.width() + left] = frame.getColor(x, y) | 0xFF000000;
				}
			}
		}
	}

	private static void drawBuffer(final Canvas canvas, final RenderContext context) {
		context.frameBitmap.setPixels(context.buffer, 0, context.image.width(), 0, 0, context.image.width(), context.image.height());
		canvas.drawBitmap(context.frameBitmap, context.drawMatrix, null);
	}

	private static void disposeBuffer(final GIFImageFrame frame, final RenderContext context) {
		switch (frame.disposalMethod()) {
			case NOT_SPECIFIED:
			case NO_DISPOSE:
				// DO NOTHING
				break;
			case RESTORE_BACKGROUND:
				for (int i = 0; i < context.buffer.length; ++i) {
					context.buffer[i] = context.image.backgroundColor();
				}
				break;
			case RESTORE_PREVIOUS:
				// TODO: implement
				break;
		}
	}

	private static void updateDrawMatrix(final RenderContext context) {
		if (context.image != null) {
			float wScale = (float) context.options.viewWidth/context.image.width();
			float hScale = (float) context.options.viewHeight/context.image.height();
			float scale = Math.min(wScale, hScale);
			context.drawMatrix.setScale(scale, scale);
			int scaledWidth = (int) (context.image.width()*scale);
			int scaledHeight = (int) (context.image.height()*scale);
			context.drawMatrix.postTranslate((context.options.viewWidth - scaledWidth)/2,
					(context.options.viewHeight - scaledHeight)/2);
		}
	}

	private enum RenderState {
		UNKNOWN,
		INIT,
		RENDERING,
		PAUSED,
		STOPPED
	}

	private static class RenderContext {
		long lastDrawTime;
		GIFImage image;
		RenderState state = RenderState.UNKNOWN;
		int[] buffer;
		Bitmap frameBitmap;
		int frameIndex = 0;
		long totalDiff = 0;
		RenderOptions options;

		final Matrix drawMatrix = new Matrix();
	}

	private static class RenderStateChange {
		private GIFImage mImage;
		private RenderState mState = RenderState.UNKNOWN;
		private int mFrameIndex = 0;
		private boolean mImageModified = false;
		private boolean mStateModified = false;
		private boolean mFrameIndexModified = false;

		RenderState getState() {
			return mState;
		}

		void setState(final RenderState state) {
			mState = state;
			mStateModified = true;
		}

		boolean isStateModified() {
			return mStateModified;
		}

		int getFrameIndex() {
			return mFrameIndex;
		}

		void setFrameIndex(final int frameIndex) {
			mFrameIndex = frameIndex;
			mFrameIndexModified = true;
		}

		boolean isFrameIndexModified() {
			return mFrameIndexModified;
		}

		GIFImage getImage() {
			 return mImage;
		}

		void setImage(final GIFImage image) {
			mImage = image;
			mImageModified = true;
		}

		boolean isImageModified() {
			return mImageModified;
		}

		boolean hasModifications() {
			return isImageModified() || isFrameIndexModified() || isStateModified();
		}
	}

	private static class RenderOptions {
		int viewWidth;
		int viewHeight;
		int gravity;
		int backgroundColor;
	}

	private static class RenderOptionsChange {
		private int mViewWidth;
		private int mViewHeight;
		private boolean mSizeModified;

		private int mGravity;
		private boolean mGravityModified;

		private int mBackgroundColor;
		private boolean mBackgroundColorModified;

		RenderOptionsChange setViewSize(int width, int height) {
			mViewWidth = width;
			mViewHeight = height;
			mSizeModified = true;
			return this;
		}

		RenderOptionsChange setGravity(int gravity) {
			mGravity = gravity;
			mGravityModified = true;
			return this;
		}

		RenderOptionsChange setBackgroundColor(int color) {
			mBackgroundColor = color;
			mBackgroundColorModified = true;
			return this;
		}

		boolean isSizeModified() {
			return mSizeModified;
		}

		boolean isGravityModified() {
			return mGravityModified;
		}

		void apply(RenderOptions options) {
			if (mSizeModified) {
				options.viewWidth = mViewWidth;
				options.viewHeight = mViewHeight;
			}
			if (mGravityModified) {
				options.gravity = mGravity;
			}
			if (mBackgroundColorModified) {
				options.backgroundColor = mBackgroundColor;
			}
		}
	}

	public enum Command {
		SET_IMAGE,
		PLAY,
		STOP,
		PAUSE
	}

	public interface CanvasHolder {
		Canvas lockCanvas();
		void unlockCanvasAndPost(Canvas canvas);
	}
}
