package com.dpforge.droidgif;

import android.content.Context;
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

public class GIFSurfaceView extends SurfaceView {
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
		int desiredWidth, desiredHeight;
		if (mImage != null) {
			desiredWidth = mImage.width();
			desiredHeight = mImage.height();
		} else {
			desiredWidth = desiredHeight = 0;
		}

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(desiredWidth, widthSize);
		} else {
			width = desiredWidth;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(desiredHeight, heightSize);
		} else {
			height = desiredHeight;
		}

		setMeasuredDimension(width, height);
	}

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

	public void play() {
		mRenderThread.executeCommand(RenderThread.Command.PLAY);
	}

	public void stop() {
		mRenderThread.executeCommand(RenderThread.Command.STOP);
	}

	public void pause() {
		mRenderThread.executeCommand(RenderThread.Command.PAUSE);
	}

	private void init() {
		getHolder().addCallback(new SurfaceHolderCallback());
		mRenderThread = new RenderThread(getHolder());
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
}