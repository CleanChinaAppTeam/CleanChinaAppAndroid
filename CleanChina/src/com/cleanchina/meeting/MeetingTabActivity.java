package com.cleanchina.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.cleanchina.R;
import com.cleanchina.app.CCTabActivity;
import com.cleanchina.meeting.fragment.AboutFragment;
import com.cleanchina.meeting.fragment.RewardFragment;
import com.cleanchina.meeting.fragment.CostFragment;
import com.cleanchina.meeting.fragment.MapFragment;
import com.cleanchina.meeting.fragment.CompanySearchFragment;

public class MeetingTabActivity extends CCTabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addTab(setIndicator(tabHost.newTabSpec("about"),
				R.drawable.ic_tab_about, "关于展会"), AboutFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("search"),
				R.drawable.ic_tab_search, "展会查询"), CompanySearchFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("map"), R.drawable.ic_tab_map,
				"展会平面图"), MapFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("award"),
				R.drawable.ic_tab_award, "金钻奖"), RewardFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("cost"), R.drawable.ic_tab_cost,
				"聚划算"), CostFragment.class);
		
		setCurrentTab(getIntent().getData().getQueryParameter("tag"));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setCurrentTab(intent.getData().getQueryParameter("tag"));
	}
	
	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equals("cost")) {
			setRightButton(R.drawable.title_how_buy, null);
			setRight2Button(R.drawable.title_where_buy, null);
		} else {
			setRightButton(0, null);
			setRight2Button(0, null);
		}
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
