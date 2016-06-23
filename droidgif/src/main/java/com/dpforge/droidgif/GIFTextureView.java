package com.dpforge.droidgif;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.TextureView;

import java.net.URL;

public class GIFTextureView extends TextureView implements GIFView {
	private final GIFViewHelper<GIFTextureView> mHelper = GIFViewHelper.create(this);

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
		ViewMeasurer.MeasureResult result = ViewMeasurer.measure(mHelper.getImage(), widthMeasureSpec,  heightMeasureSpec);
		setMeasuredDimension(result.width, result.height);
	}

	@Override
	public void setRawResource(@RawRes int resId) {
		mHelper.setRawResource(resId);
	}

	@Override
	public void setImageURL(final URL url) {
		mHelper.setImageURL(url);
	}

	@Override
	public void play() {
		mHelper.play();
	}

	@Override
	public void stop() {
		mHelper.stop();
	}

	@Override
	public void pause() {
		mHelper.pause();
	}

	private void init() {
		mHelper.init();
	}
}
