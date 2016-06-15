package com.dpforge.droidgif;

import android.view.View;

import com.dpforge.droidgif.decoder.GIFImage;

class ViewMeasurer {
	private ViewMeasurer() {
	}

	static MeasureResult measure(final GIFImage gif, final int widthMeasureSpec, final int heightMeasureSpec) {
		int desiredWidth, desiredHeight;
		if (gif != null) {
			desiredWidth = gif.width();
			desiredHeight = gif.height();
		} else {
			desiredWidth = desiredHeight = 0;
		}

		int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == View.MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == View.MeasureSpec.AT_MOST) {
			width = Math.min(desiredWidth, widthSize);
		} else {
			width = desiredWidth;
		}

		if (heightMode == View.MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == View.MeasureSpec.AT_MOST) {
			height = Math.min(desiredHeight, heightSize);
		} else {
			height = desiredHeight;
		}

		return new MeasureResult(width, height);
	}

	final static class MeasureResult {
		final int width;
		final int height;

		private MeasureResult(final int width, final int height) {
			this.width = width;
			this.height = height;
		}
	}
}
