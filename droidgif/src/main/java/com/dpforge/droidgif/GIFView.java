package com.dpforge.droidgif;

import android.support.annotation.RawRes;

import java.net.URL;

public interface GIFView {
	void setRawResource(@RawRes int resId);

	void setImageURL(URL url);

	void play();

	void stop();

	void pause();
}
