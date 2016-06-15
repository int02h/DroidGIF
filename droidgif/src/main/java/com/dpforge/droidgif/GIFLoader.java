package com.dpforge.droidgif;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RawRes;

import com.dpforge.droidgif.decoder.DecoderException;
import com.dpforge.droidgif.decoder.GIFDecoder;
import com.dpforge.droidgif.decoder.GIFImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class GIFLoader {
	private final static int MAX_THREAD_COUNT = 5;

	private static GIFLoader mInstance;

	private final Executor mExecutor;
	private final Handler mMainHandler;

	private GIFLoader() {
		mExecutor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		mMainHandler = new Handler(Looper.getMainLooper());
	}

	synchronized static GIFLoader getInstance() {
		if (mInstance == null) {
			mInstance = new GIFLoader();
		}
		return mInstance;
	}

	void loadInputStream(final InputStream is, final OnLoadListener listener) {
		if (listener == null)
			return;
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				BufferedInputStream bis = null;
				try {
					bis = (is instanceof BufferedInputStream)
							? (BufferedInputStream) is
							: new BufferedInputStream(is);
					final GIFDecoder decoder = new GIFDecoder(bis);
					final GIFImage image = decoder.decode();
					invokeListener(listener, image, null);
				} catch (IOException | DecoderException ex) {
					invokeListener(listener, null, ex);
				} finally {
					safeClose(bis);
				}
			}
		});
	}

	void loadRawResource(final Context context, @RawRes final int resId, final OnLoadListener listener) {
		if (listener == null)
			return;
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final InputStream is = context.getResources().openRawResource(resId);
				loadInputStream(is, listener);
			}
		});
	}

	void loadURL(final URL url, final OnLoadListener listener) {
		if (listener == null)
			return;
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = url.openConnection().getInputStream();
					loadInputStream(is, listener);
				} catch (IOException ex) {
					invokeListener(listener, null, ex);
				}
			}
		});
	}

	private void invokeListener(final OnLoadListener listener, final GIFImage image, final Exception ex) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				listener.onLoad(image, ex);
			}
		});
	}

	private static void safeClose(final InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException ignored) {
			}
		}
	}

	interface OnLoadListener {
		void onLoad(final GIFImage image, final Exception ex);
	}
}
