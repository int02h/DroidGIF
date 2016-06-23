package com.dpforge.droidgif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dpforge.droidgif.decoder.GIFImage;

import java.net.URL;

public class GIFSurfaceView extends SurfaceView implements GIFView {
	private RenderThread mRenderThread;
	private GIFImage mImage;

	private final GIFLoader.OnLoadListener mLoadListener = new GIFLoader.OnLoadListener() {
		@Override
		public void onLoad(final GIFImage image, final Exception ex) {
			mImage = image;
			mRenderThread.executeCommand(RenderThread.Command.SET_IMAGE, image);
			requestLayout();
			invalidate();
		}
	};

	public GIFSurfaceView(final Context context) {
		super(context);
		init();
	}

	public GIFSurfaceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		ViewMeasurer.MeasureResult result = ViewMeasurer.measure(mImage, widthMeasureSpec,  heightMeasureSpec);
		setMeasuredDimension(result.width, result.height);
	}

	@Override
	public void setRawResource(@RawRes int resId) {
		GIFLoader.getInstance().loadRawResource(getContext(), resId, mLoadListener);
	}

	@Override
	public void setImageURL(final URL url) {
		GIFLoader.getInstance().loadURL(url, mLoadListener);
	}

	@Override
	public void play() {
		mRenderThread.executeCommand(RenderThread.Command.PLAY);
	}

	@Override
	public void stop() {
		mRenderThread.executeCommand(RenderThread.Command.STOP);
	}

	@Override
	public void pause() {
		mRenderThread.executeCommand(RenderThread.Command.PAUSE);
	}

	private void init() {
		getHolder().addCallback(new SurfaceHolderCallback());
		mRenderThread = new RenderThread(new CanvasHolder(getHolder()));

		Drawable bg = getBackground();
		if (bg instanceof ColorDrawable) {
			ColorDrawable cd = (ColorDrawable) bg;
			mRenderThread.setBackgroundColor(cd.getColor());
		}
	}

	private class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			mRenderThread.setRunning(true);
			mRenderThread.start();
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
			mRenderThread.setViewSize(width, height);
		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			boolean retry = true;
			mRenderThread.setRunning(false);
			while (retry) {
				try {
					mRenderThread.join();
					retry = false;
				} catch (InterruptedException ignored) {

				}
			}
		}
	}

	private class CanvasHolder implements RenderThread.CanvasHolder {
		private final SurfaceHolder mHolder;

		private CanvasHolder(final SurfaceHolder holder) {
			mHolder = holder;
		}

		@Override
		public Canvas lockCanvas() {
			return mHolder.lockCanvas();
		}

		@Override
		public void unlockCanvasAndPost(final Canvas canvas) {
			mHolder.unlockCanvasAndPost(canvas);
		}
	}
}
