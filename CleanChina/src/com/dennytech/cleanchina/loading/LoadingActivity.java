package com.dennytech.cleanchina.loading;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.cleanchina.R;
import com.dennytech.cleanchina.loading.view.LoadingWaveView;
import com.dennytech.cleanchina.loading.view.LoadingWaveView.LoadingHandler;

public class LoadingActivity extends Activity implements LoadingHandler {
	
	private LoadingWaveView waveView;
	private TextView percentView;

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
	}


}
