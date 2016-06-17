package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.decoder.lzw.LZW;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FrameDecoder {
	private final List<GIFDecodedFrame> mFrames;
	private final BlockingQueue<GIFImageFrame> mFrameToDecode;
	private boolean mDecoding;
	private boolean mStopWhenEmpty;

	FrameDecoder() {
		mFrames = new ArrayList<>(32);
		mFrameToDecode = new LinkedBlockingDeque<>();
	}

	public GIFDecodedFrame getFrame(int index) {
		synchronized (mFrames) {
			while (index >= mFrames.size()) {
				try {
					mFrames.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return mFrames.get(index);
		}
	}

	void addFrameForDecoding(final GIFImageFrame frame) {
		try {
			mFrameToDecode.put(frame);
			if (!mDecoding) {
				startDecoding();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void stop() {
		mStopWhenEmpty = true;
	}

	private void startDecoding() {
		if (mDecoding)
			return;

		mDecoding = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mDecoding) {
					try {
						if (mStopWhenEmpty && mFrameToDecode.isEmpty()) {
							mDecoding = false;
							break;
						}

						final GIFImageFrame frame = mFrameToDecode.take();
						if (frame != null) {
							final byte[] colorIndices = new byte[frame.width()*frame.height()];
							LZW.decompress(new ByteArrayInputStream(frame.compressedData()),
									frame.minCodeSize() + 1, colorIndices);
							synchronized (mFrames) {
								mFrames.add(new GIFDecodedFrame(frame, colorIndices));
								mFrames.notifyAll();
							}
						} else {
							mDecoding = false;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
