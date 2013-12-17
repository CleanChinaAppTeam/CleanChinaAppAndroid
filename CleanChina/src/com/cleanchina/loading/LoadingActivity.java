package com.cleanchina.loading;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.loading.view.LoadingWaveView;
import com.cleanchina.loading.view.LoadingWaveView.LoadingHandler;

public class LoadingActivity extends CCActivity implements LoadingHandler {

	private LoadingWaveView waveView;
	private TextView percentView;

	private Handler forwardHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://home")));
			finish();
		};
	};

	protected int customTitleType() {
		return Window.FEATURE_NO_TITLE;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		waveView = (LoadingWaveView) findViewById(R.id.loading_wave);
		percentView = (TextView) findViewById(R.id.loading_percent);

		waveView.startAnimation(this, 2000, 0);
	}

	@Override
	public void onLoading(int count, int total) {
		percentView.setText(count + "%");

		if (count == 100) {
			forwardHandler.removeMessages(0);
			forwardHandler.sendEmptyMessageDelayed(0, 100);
		}
	}

}
