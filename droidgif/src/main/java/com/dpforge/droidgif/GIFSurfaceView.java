package com.dpforge.droidgif;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.net.URL;

public class GIFSurfaceView extends SurfaceView implements GIFView {
	private final GIFViewHelper<GIFSurfaceView> mHelper = GIFViewHelper.create(this);

	public GIFSurfaceView(final Context context) {
		super(context);
		init();
	}

	public GIFSurfaceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GIFSurfaceView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
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
