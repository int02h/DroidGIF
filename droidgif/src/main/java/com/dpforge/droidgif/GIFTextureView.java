package com.dpforge.droidgif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.TextureView;

import com.dpforge.droidgif.decoder.GIFImage;

import java.net.URL;

public class GIFTextureView extends TextureView implements GIFView {
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
