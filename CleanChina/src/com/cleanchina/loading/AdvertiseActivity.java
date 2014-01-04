package com.cleanchina.loading;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cleanchina.app.CCActivity;

public class AdvertiseActivity extends CCActivity {
	
	private Handler forwardHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://home")));
			finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AdvertiseManager manager = new AdvertiseManager(this, mapiService(),
				imageService());
		Bitmap adv = manager.read();
		if (adv == null) {
			finish();
			return;
		}
		
		ImageView iv = new ImageView(this);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setImageBitmap(adv);
		setContentView(iv);
		
		forwardHandler.removeMessages(0);
		forwardHandler.sendEmptyMessageDelayed(0, 1500);
	}
	
	@Override
	protected int customTitleType() {
		return Window.FEATURE_NO_TITLE;
	}

}
