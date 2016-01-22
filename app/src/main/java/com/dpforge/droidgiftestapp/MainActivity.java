package com.dpforge.droidgiftestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dpforge.droidgif.GIFImageView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final GIFImageView gif = new GIFImageView(this);
		gif.setImageResource(R.raw.earthquake);
		setContentView(gif);
	}
}
