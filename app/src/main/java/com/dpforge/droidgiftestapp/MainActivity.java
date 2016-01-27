package com.dpforge.droidgiftestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dpforge.droidgif.GIFImageView;

public class MainActivity extends AppCompatActivity {
	private GIFImageView[] mGifViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGifViews = new GIFImageView[]{
				(GIFImageView) findViewById(R.id.gif1),
				(GIFImageView) findViewById(R.id.gif2),
				(GIFImageView) findViewById(R.id.gif3)
		};
	}

	public void onLoadGifsClick(View view) {
		mGifViews[0].setImageResource(R.raw.big_with_comment);
		mGifViews[1].setImageResource(R.raw.rotating_earth);
		mGifViews[2].setImageResource(R.raw.earthquake);
	}
}
