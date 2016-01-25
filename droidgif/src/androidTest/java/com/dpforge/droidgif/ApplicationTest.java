package com.dpforge.droidgif;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.dpforge.droidgif.decoder2.GIFDecoder;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	public ApplicationTest() {
		super(Application.class);
	}

	public void testPerformanceImproved() throws Exception {
		long startTime = System.currentTimeMillis();
		int count = 100;
		for (int i = 0; i < count; ++i) {
			InputStream inputStream = getContext().getResources().openRawResource(R.raw.earthquake);
			final GIFDecoder decoder = new GIFDecoder(new BufferedInputStream(inputStream));
			decoder.decode();
		}
		long elapsed = System.currentTimeMillis() - startTime;
		Log.i("_PERF_", "Improved elapsed time " + elapsed + "ms");
		Log.i("_PERF_", "Improved average time " + (elapsed / count) + "ms");
	}
}