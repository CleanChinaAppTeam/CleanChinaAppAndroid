package com.dennytech.cleanchina.loading.view;

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
		super.onDetachedFromWindow();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		waveBmp = decodeBmp(R.drawable.loading_waves);
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
		int imageWidth = waveBmp.getWidth();

		double percent = (double) spendTime / totalTime;
		
		int left = (int) ((imageWidth - viewWidth + 300) * percent);
		canvas.drawBitmap(waveBmp, -left, 0, null);
		
		if (totalTime < spendTime || (imageWidth - left) < (viewWidth - 130)) {
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
