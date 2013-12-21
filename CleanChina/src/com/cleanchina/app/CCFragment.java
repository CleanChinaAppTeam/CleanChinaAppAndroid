package com.cleanchina.app;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cleanchina.R;
import com.cleanchina.widget.TitleBar;
import com.dennytech.common.app.BDFragment;

/**
 * base fragment
 * 
 * @author dengjun86
 * 
 */
public class CCFragment extends BDFragment {

	private TitleBar titleBar;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		titleBar = (TitleBar) view.findViewById(R.id.titlebar);
	}

	public void setTitle(String title) {
		if (titleBar != null) {
			titleBar.setTitle(title.toString());
		} else {
			getActivity().setTitle(title);
		}

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

	public void setTitle(int resId) {
		setTitle(getString(resId));
	}
}
