package com.dpforge.droidgif;

import android.support.annotation.RawRes;

public interface GIFView {
	void setRawResource(@RawRes int resId);

	void play();

	void stop();

	void pause();
}
