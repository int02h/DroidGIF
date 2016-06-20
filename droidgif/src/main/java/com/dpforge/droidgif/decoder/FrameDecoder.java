package com.dpforge.droidgif.decoder;

import com.dpforge.droidgif.decoder.lzw.LZW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FrameDecoder {
	private final List<GIFImageFrame> mDecodedFrames;
	private final BlockingQueue<GIFImageFrame> mFrameToDecode;
	private boolean mDecoding;
	private boolean mStopWhenEmpty;

	FrameDecoder() {
		mDecodedFrames = new ArrayList<>(32);
		mFrameToDecode = new LinkedBlockingDeque<>();
	}

	public GIFImageFrame getFrame(int index) {
		synchronized (mDecodedFrames) {
			while (index >= mDecodedFrames.size()) {
				try {
					mDecodedFrames.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return mDecodedFrames.get(index);
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
				final LZW lzw = new LZW();
				while (mDecoding) {
					try {
						if (mStopWhenEmpty && mFrameToDecode.isEmpty()) {
							mDecoding = false;
							break;
						}

						final GIFImageFrame frame = mFrameToDecode.take();
						if (frame != null) {
							final byte[] colorIndices = new byte[frame.width()*frame.height()];
							lzw.decompress(frame.compressedDataStream(), frame.minCodeSize() + 1, colorIndices);
							frame.setDecoded(colorIndices);
							synchronized (mDecodedFrames) {
								mDecodedFrames.add(frame);
								mDecodedFrames.notifyAll();
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
