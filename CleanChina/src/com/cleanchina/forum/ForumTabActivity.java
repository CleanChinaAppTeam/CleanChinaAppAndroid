package com.cleanchina.forum;

import android.os.Bundle;
import android.view.Window;

import com.cleanchina.R;
import com.cleanchina.app.CCTabActivity;

public class ForumTabActivity extends CCTabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addTab(setIndicator(tabHost.newTabSpec("search"),
				R.drawable.ic_tab_about, "论坛搜索"), ForumSearchFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("today"),
				R.drawable.ic_tab_search, "今日论坛"), ForumTodayFragment.class);
	}

	@Override
	protected int getContentViewResId() {
		return R.layout.activity_tab_meeting;
	}

	@Override
	protected int customTitleType() {
		return Window.FEATURE_CUSTOM_TITLE;
	}

}
