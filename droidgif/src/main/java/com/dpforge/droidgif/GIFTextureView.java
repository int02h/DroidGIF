package com.dpforge.droidgif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.TextureView;

import com.dpforge.droidgif.decoder.DecoderException;
import com.dpforge.droidgif.decoder.GIFDecoder;
import com.dpforge.droidgif.decoder.GIFImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GIFTextureView extends TextureView implements GIFView {
	private RenderThread mRenderThread;
	private GIFImage mImage;

	public GIFTextureView(Context context) {
		super(context);
		init();
	}

	public GIFTextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GIFTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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
		} catch (IOException | DecoderException e) {
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
		setSurfaceTextureListener(new SurfaceListener());
		mRenderThread = new RenderThread(new CanvasHolder());
	}

	private class SurfaceListener implements TextureView.SurfaceTextureListener {

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			mRenderThread.setRunning(true);
			mRenderThread.start();
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			boolean retry = true;
			mRenderThread.setRunning(false);
			while (retry) {
				try {
					mRenderThread.join();
					retry = false;
				} catch (InterruptedException ignored) {

				}
			}
			return true;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	}

	private class CanvasHolder implements RenderThread.CanvasHolder {
		@Override
		public Canvas lockCanvas() {
			return GIFTextureView.this.lockCanvas();
		}

		@Override
		public void unlockCanvasAndPost(final Canvas canvas) {
			GIFTextureView.this.unlockCanvasAndPost(canvas);
		}
	}
}
