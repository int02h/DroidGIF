package com.dpforge.droidgif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.dpforge.droidgif.decoder.GIFImage;
import com.dpforge.droidgif.decoder.GIFImageFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RenderThread extends Thread {
	private final static int MAX_COMMANDS = 32;

	private final SurfaceHolder mHolder;
	private volatile boolean mRunning;

	private final List<CommandInfo> mCommands = new ArrayList<>(4);

	RenderThread(final SurfaceHolder holder) {
		mHolder = holder;
	}

	void setRunning(boolean running) {
		mRunning = running;
	}

	void executeCommand(final Command command) {
		executeCommand(command, null);
	}

	void executeCommand(final Command command, final GIFImage image) {
		synchronized (mCommands) {
			if (mCommands.size() + 1 < MAX_COMMANDS) {
				mCommands.add(new CommandInfo(command, image));
			}
		}
	}

	@Override
	public void run() {
		final RenderContext context = new RenderContext();
		context.lastDrawTime = System.currentTimeMillis();
		context.state = RenderState.UNKNOWN;
		context.frameIndex = 0;
		context.totalDiff = 0;

		while (mRunning) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				draw(canvas, context);
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}

			synchronized (mCommands) {
				for (final CommandInfo cmd : mCommands) {
					processCommand(cmd, context);
				}
				mCommands.clear();
			}
		}
	}

	private static void processCommand(final CommandInfo commandInfo, final RenderContext context) {
		if (commandInfo != null) {
			if (canExecuteCommand(commandInfo.command, context.state)) {
				switch (commandInfo.command) {
					case SET_IMAGE:
						context.image = commandInfo.data;
						context.buffer = new int[context.image.height()*context.image.width()];
						context.frameBitmap = Bitmap.createBitmap(context.image.width(),
								context.image.height(), Bitmap.Config.ARGB_8888);
						context.state = RenderState.INIT;
						break;
					case START:
						context.frameIndex = 0;
						context.totalDiff = 0;
						context.state = RenderState.RENDERING;
						break;
					case STOP:
						context.frameIndex = 0;
						context.totalDiff = 0;
						context.state = RenderState.STOPPED;
						break;
					case RESUME:
						context.state = RenderState.RENDERING;
						break;
					case PAUSE:
						context.state = RenderState.PAUSED;
						break;
				}
			}
		}
	}

	private static boolean canExecuteCommand(final Command command, final RenderState state) {
		switch (command) {
			case SET_IMAGE:
				return true;
			case START:
				return (state == RenderState.INIT || state == RenderState.STOPPED);
			case STOP:
				return (state == RenderState.PAUSED || state == RenderState.RENDERING);
			case RESUME:
				return (state == RenderState.PAUSED);
			case PAUSE:
				return (state == RenderState.RENDERING);
			default:
				return false;
		}
	}

	private static void draw(final Canvas canvas, final RenderContext context) {
		if (context.image != null) {
			switch (context.state) {
				case RENDERING:
					if (canvas != null) {
						final GIFImageFrame frame = context.image.getFrame(context.frameIndex);
						prepareBuffer(frame, context);
						drawBuffer(canvas, context);
						disposeBuffer(frame, context);

						context.totalDiff += System.currentTimeMillis() - context.lastDrawTime;
						context.lastDrawTime = System.currentTimeMillis();

						if (context.totalDiff >= frame.delay()*10) {
							context.frameIndex = (context.frameIndex + 1)%context.image.framesCount();
							context.totalDiff = 0;
						}
					}
					break;
				case PAUSED:
					if (canvas != null) {
						final GIFImageFrame frame = context.image.getFrame(context.frameIndex);
						prepareBuffer(frame, context);
						drawBuffer(canvas, context);
						disposeBuffer(frame, context);
					}
					break;
				case STOPPED:
				case INIT:
					if (canvas != null) {
						Arrays.fill(context.buffer, 0xFF000000);
						drawBuffer(canvas, context);
					}
					break;
			}
		}
	}

	private static void prepareBuffer(final GIFImageFrame frame, final RenderContext context) {
		for (int y = 0; y < frame.height(); ++y) {
			for (int x = 0; x < frame.width(); ++x) {
				int left = frame.left() + x;
				int top = frame.top() + y;
				if (!frame.isTransparentPixel(x, y)) {
					context.buffer[top*context.image.width() + left] = frame.getColor(x, y) | 0xFF000000;
				}
			}
		}
	}

	private static void drawBuffer(final Canvas canvas, final RenderContext context) {
		context.frameBitmap.setPixels(context.buffer, 0, context.image.width(), 0, 0, context.image.width(), context.image.height());
		canvas.drawBitmap(context.frameBitmap, 0, 0, null);
	}

	private static void disposeBuffer(final GIFImageFrame frame, final RenderContext context) {
		switch (frame.disposalMethod()) {
			case NOT_SPECIFIED:
			case NO_DISPOSE:
				// DO NOTHING
				break;
			case RESTORE_BACKGROUND:
				for (int i = 0; i < context.buffer.length; ++i) {
					context.buffer[i] = context.image.backgroundColor();
				}
				break;
			case RESTORE_PREVIOUS:
				// TODO: implement
				break;
		}
	}

	private enum RenderState {
		UNKNOWN,
		INIT,
		RENDERING,
		PAUSED,
		STOPPED
	}

	private static class CommandInfo {
		final Command command;
		final GIFImage data;

		private CommandInfo(final Command command, final GIFImage data) {
			this.command = command;
			this.data = data;
		}
	}

	private static class RenderContext {
		long lastDrawTime;
		GIFImage image;
		RenderState state = RenderState.UNKNOWN;
		int[] buffer;
		Bitmap frameBitmap;
		int frameIndex = 0;
		long totalDiff = 0;
	}

	public enum Command {
		SET_IMAGE,
		START,
		STOP,
		RESUME,
		PAUSE
	}
}
