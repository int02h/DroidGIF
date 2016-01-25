package com.dpforge.droidgif;

import android.content.Context;
import android.support.annotation.RawRes;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dpforge.droidgif.decoder2.DecoderException;
import com.dpforge.droidgif.decoder2.GIFDecoder;
import com.dpforge.droidgif.decoder2.GIFImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GIFImageView extends SurfaceView {
	private RendererThread mRendererThread;

	public GIFImageView(final Context context) {
		super(context);
		getHolder().addCallback(new SurfaceHolderCallback());
		mRendererThread = new RendererThread(getHolder());
	}

	public void setImageResource(@RawRes int resId) {
		final InputStream is = getContext().getResources().openRawResource(resId);
		final BufferedInputStream bis = new BufferedInputStream(is);
		try {
			final GIFDecoder decoder = new GIFDecoder(bis);
			final GIFImage image = decoder.decode();
			bis.close();
			mRendererThread.setImage(image);
		} catch (IOException | DecoderException  e) {
			e.printStackTrace();
		}
	}

	private class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			mRendererThread.setRunning(true);
			mRendererThread.start();
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {

		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			boolean retry = true;
			mRendererThread.setRunning(false);
			while (retry) {
				try {
					mRendererThread.join();
					retry = false;
				} catch (InterruptedException ignored) {

				}
			}
		}
	}
}
