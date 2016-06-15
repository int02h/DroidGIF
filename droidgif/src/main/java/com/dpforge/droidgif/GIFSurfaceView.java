package com.dpforge.droidgif;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dpforge.droidgif.decoder.DecoderException;
import com.dpforge.droidgif.decoder.GIFDecoder;
import com.dpforge.droidgif.decoder.GIFImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GIFSurfaceView extends SurfaceView implements GIFView {
	private RenderThread mRenderThread;
	private GIFImage mImage;

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
	public void setImageResource(@RawRes int resId) {
		final InputStream is = getContext().getResources().openRawResource(resId);
		final BufferedInputStream bis = new BufferedInputStream(is);
		try {
			final GIFDecoder decoder = new GIFDecoder(bis);
			mImage = decoder.decode();
			bis.close();
			mRenderThread.executeCommand(RenderThread.Command.SET_IMAGE, mImage);
			requestLayout();
			invalidate();
		} catch (IOException | DecoderException  e) {
			e.printStackTrace();
		}
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
	}

	private class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			mRenderThread.setRunning(true);
			mRenderThread.start();
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {

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
