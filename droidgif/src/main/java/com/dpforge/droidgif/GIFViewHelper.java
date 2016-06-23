package com.dpforge.droidgif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.RawRes;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

import com.dpforge.droidgif.decoder.GIFImage;

import java.net.URL;

abstract class GIFViewHelper<T extends View & GIFView> implements GIFView {
	protected final T mView;

	private RenderThread mRenderThread;
	private GIFImage mImage;

	private final GIFLoader.OnLoadListener mLoadListener = new GIFLoader.OnLoadListener() {
		@Override
		public void onLoad(final GIFImage image, final Exception ex) {
			mImage = image;
			mRenderThread.executeCommand(RenderThread.Command.SET_IMAGE, image);
			mView.requestLayout();
			mView.invalidate();
		}
	};

	protected GIFViewHelper(final T view) {
		mView = view;
	}

	GIFImage getImage() {
		return mImage;
	}

	abstract void init();
	abstract RenderThread.CanvasHolder getCanvasHolder();

	private Context getContext() {
		return mView.getContext();
	}

	/**********************************/
	/*     GIFView implementation     */
	/**********************************/

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

	/*****************************/
	/*     Surface callbacks     */
	/*****************************/

	protected void onSurfaceCreated() {
		mRenderThread = new RenderThread(getCanvasHolder());

		Drawable bg = mView.getBackground();
		if (bg instanceof ColorDrawable) {
			ColorDrawable cd = (ColorDrawable) bg;
			mRenderThread.setBackgroundColor(cd.getColor());
		}

		mRenderThread.setRunning(true);
		mRenderThread.start();
	}

	protected void onSurfaceSizeChanged(int width, int height) {
		mRenderThread.setViewSize(width, height);
	}

	protected void onSurfaceDestroyed() {
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

	/****************************/
	/*     Static factories     */
	/****************************/

	static GIFViewHelper<GIFTextureView> create(GIFTextureView view) {
		return new TextureViewHelper(view);
	}

	static GIFViewHelper<GIFSurfaceView> create(GIFSurfaceView view) {
		return new SurfaceViewHelper(view);
	}

	/**************************************/
	/*     TextureView implementation     */
	/**************************************/

	private final static class TextureViewHelper extends GIFViewHelper<GIFTextureView> {
		public TextureViewHelper(final GIFTextureView view) {
			super(view);
		}

		@Override
		void init() {
			mView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
				@Override
				public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
					onSurfaceCreated();
					onSurfaceSizeChanged(width, height);
				}

				@Override
				public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {
					onSurfaceSizeChanged(width, height);
				}

				@Override
				public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
					onSurfaceDestroyed();
					return true;
				}

				@Override
				public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
					// Nothing to do
				}
			});
		}

		@Override
		RenderThread.CanvasHolder getCanvasHolder() {
			return new RenderThread.CanvasHolder() {
				@Override
				public Canvas lockCanvas() {
					return mView.lockCanvas();
				}

				@Override
				public void unlockCanvasAndPost(final Canvas canvas) {
					mView.unlockCanvasAndPost(canvas);
				}
			};
		}
	}

	/**************************************/
	/*     SurfaceView implementation     */
	/**************************************/

	private final static class SurfaceViewHelper extends GIFViewHelper<GIFSurfaceView> {
		public SurfaceViewHelper(final GIFSurfaceView view) {
			super(view);
		}

		@Override
		void init() {
			mView.getHolder().addCallback(new SurfaceHolder.Callback() {
				@Override
				public void surfaceCreated(final SurfaceHolder holder) {
					onSurfaceCreated();
				}

				@Override
				public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
					onSurfaceSizeChanged(width, height);
				}

				@Override
				public void surfaceDestroyed(final SurfaceHolder holder) {
					onSurfaceDestroyed();
				}
			});
		}

		@Override
		RenderThread.CanvasHolder getCanvasHolder() {
			final SurfaceHolder holder = mView.getHolder();
			return new RenderThread.CanvasHolder() {
				@Override
				public Canvas lockCanvas() {
					return holder.lockCanvas();
				}

				@Override
				public void unlockCanvasAndPost(final Canvas canvas) {
					holder.unlockCanvasAndPost(canvas);
				}
			};
		}
	}
}