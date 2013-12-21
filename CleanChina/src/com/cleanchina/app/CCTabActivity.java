package com.cleanchina.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.cleanchina.R;

public class CCTabActivity extends CCActivity {

	protected FragmentTabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewResId());
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	}

	protected int getContentViewResId() {
		return R.layout.activity_tab_bottom;
	}

	protected void addTab(TabSpec tabSpec, Class<?> fragment) {
		tabHost.addTab(tabSpec, fragment, null);
	}
	
	public void setCurrentTab(String tag) {
		tabHost.setCurrentTabByTag(tag);
	}

	public TabSpec setIndicatorImage(TabSpec spec, int resid) {
		ImageView v = new ImageView(this);
		v.setImageResource(resid);
		return spec.setIndicator(v);
	}

	public TabSpec setIndicator(TabSpec spec, int icon, String title) {
		View v = getLayoutInflater().inflate(R.layout.layout_tab_item, null);
		TextView titleTv = (TextView) v.findViewById(R.id.title);
		titleTv.setText(title);
		ImageView iconIv = (ImageView) v.findViewById(R.id.icon);
		iconIv.setImageResource(icon);
		return spec.setIndicator(v);
	}

}
