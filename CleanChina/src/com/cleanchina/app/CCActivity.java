package com.cleanchina.app;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.cleanchina.R;
import com.cleanchina.widget.TitleBar;

public class CCActivity extends FragmentActivity {
	
	private TitleBar titleBar;

	/**
	 * custom title type
	 * 
	 * @return
	 */
	protected int customTitleType() {
		return Window.FEATURE_CUSTOM_TITLE;
	}

	@Override
	public void setContentView(int layoutResID) {
		requestWindowFeature(customTitleType());
		super.setContentView(layoutResID);
		if (customTitleType() == Window.FEATURE_CUSTOM_TITLE) {
			initCustomTitle();
		}
	}

	@Override
	public void setContentView(View view) {
		requestWindowFeature(customTitleType());
		super.setContentView(view);
		if (customTitleType() == Window.FEATURE_CUSTOM_TITLE) {
			initCustomTitle();
		}
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		requestWindowFeature(customTitleType());
		super.setContentView(view, params);
		if (customTitleType() == Window.FEATURE_CUSTOM_TITLE) {
			initCustomTitle();
		}
	}

	protected void initCustomTitle() {
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		titleBar = (TitleBar) findViewById(R.id.titlebar);
		enableBackButton(true);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		titleBar.setTitle(title.toString());
	}

	public void setLeftButton(int resId, OnClickListener listener) {
		titleBar.setLeftButton(resId, listener);
	}

	public void setRightButton(int resId, OnClickListener listener) {
		titleBar.setRightButton(resId, listener);
	}

	public void enableBackButton(boolean enable) {
		titleBar.enableBackButton(enable);
	}

}
