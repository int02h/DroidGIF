package com.dpforge.droidgiftestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dpforge.droidgif.GIFSurfaceView;
import com.dpforge.droidgif.GIFView;

public class MainActivity extends AppCompatActivity {
	private GIFView mGifView;
	private int mGifIndex = 0;
	private int[] mGifList = new int[] {
			R.raw.big_with_comment, R.raw.rotating_earth, R.raw.earthquake
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGifView = (GIFView) findViewById(R.id.gif);
	}

	public void onNextGifClick(View view) {
		mGifView.setImageResource(mGifList[mGifIndex]);
		mGifIndex = (mGifIndex + 1)%mGifList.length;
	}

	public void onPlayClick(View view) {
		mGifView.play();
	}

	public void onStopClick(View view) {
		mGifView.stop();
	}

	public void onPauseClick(View view) {
		mGifView.pause();
	}
}
