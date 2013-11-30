package com.cleanchina.loading.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.cleanchina.R;

public class LoadingWaveView extends View {

	private static final int PERIOD = 10;
	private Bitmap waveBmp;
	private Bitmap bigBubble;
	private Bitmap smallBubble;
	private Timer timer;

	private LoadingHandler loadingHandler;
	private long totalTime;
	private long spendTime;
	private long lastTime = System.currentTimeMillis();

	public LoadingWaveView(Context context) {
		this(context, null);
	}

	public LoadingWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (waveBmp != null) {
			waveBmp.recycle();
		}
		if (bigBubble != null) {
			bigBubble.recycle();
		}
		if (smallBubble != null) {
			smallBubble.recycle();
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		waveBmp = decodeBmp(R.drawable.loading_waves);
		bigBubble = decodeBmp(R.drawable.loading_big_bubble);
		smallBubble = decodeBmp(R.drawable.loading_small_bubble);
	}

	private Bitmap decodeBmp(int drawable) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ALPHA_8;
		options.inScaled = true;
		return BitmapFactory.decodeResource(getResources(), drawable, options);
	}

	public void startAnimation(LoadingHandler handler, long duration, long delay) {
		this.loadingHandler = handler;
		this.totalTime = duration;
		timer = new Timer("loading");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				postInvalidate();
			}
		}, delay, PERIOD);
	}

	public void stopAnimation() {
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		spendTime += PERIOD;

		int viewWidth = getWidth();
		int viewHeight = getHeight();
		int waveWidth = waveBmp.getWidth();
		int bubbleWidth = bigBubble.getWidth();

		double percent = (double) spendTime / totalTime;

		int left = (int) ((waveWidth - viewWidth + 300) * percent);
		canvas.drawBitmap(waveBmp, -left, -15, null);
		canvas.drawBitmap(bigBubble, (viewWidth - bubbleWidth) / 2,
				(int) (viewHeight - viewHeight * percent), null);
		canvas.drawBitmap(smallBubble, (viewWidth - bubbleWidth) / 2,
				(int) (viewHeight - viewHeight * (percent + 0.15)), null);

		if (totalTime < spendTime || (waveWidth - left) < (viewWidth - 120)) {
			stopAnimation();
			loadingHandler.onLoading(100, 100);
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > 100) {
			int count = (int) (percent * 100);
			loadingHandler.onLoading(count, 100);
			lastTime = currentTime;
		}
	}

	public interface LoadingHandler {
		void onLoading(int count, int total);
	}

}
