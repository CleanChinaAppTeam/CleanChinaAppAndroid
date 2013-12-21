package com.cleanchina;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cleanchina.app.CCActivity;

public class HomeActivity extends CCActivity implements OnClickListener {

	private View newsBtn;
	private View marketBtn;
	private View meetingBtn;
	private View ebookBtn;
	private View youkuBtn;
	private View wechatBtn;
	private View weiboBtn;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_home);
		newsBtn = findViewById(R.id.home_news);
		marketBtn = findViewById(R.id.home_market);
		meetingBtn = findViewById(R.id.home_meeting);
		ebookBtn = findViewById(R.id.home_ebook);
		youkuBtn = findViewById(R.id.home_youku);
		wechatBtn = findViewById(R.id.home_wechat);
		weiboBtn = findViewById(R.id.home_weibo);

		newsBtn.setOnClickListener(this);
		marketBtn.setOnClickListener(this);
		meetingBtn.setOnClickListener(this);
		ebookBtn.setOnClickListener(this);
		youkuBtn.setOnClickListener(this);
		wechatBtn.setOnClickListener(this);
		weiboBtn.setOnClickListener(this);
	}

	@Override
	protected int customTitleType() {
		return Window.FEATURE_NO_TITLE;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.home_meeting) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://meeting")));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
