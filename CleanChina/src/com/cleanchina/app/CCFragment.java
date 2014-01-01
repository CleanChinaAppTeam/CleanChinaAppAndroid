package com.cleanchina.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cleanchina.R;
import com.cleanchina.widget.TitleBar;
import com.dennytech.common.app.CLFragment;

/**
 * base fragment
 * 
 * @author dengjun86
 * 
 */
public class CCFragment extends CLFragment {

	private TitleBar titleBar;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		titleBar = (TitleBar) view.findViewById(R.id.titlebar);
		if (titleBar == null) {
			titleBar = (TitleBar) getActivity().findViewById(R.id.titlebar);
		}
	}

	public void setTitle(String title) {
		if (titleBar != null) {
			titleBar.setTitle(title.toString());
		} else {
			getActivity().setTitle(title);
		}

	}

	public void setLeftButton(int resId, OnClickListener listener) {
		if (titleBar != null) {
			titleBar.setLeftButton(resId, listener);
		}
	}

	public void setRightButton(int resId, OnClickListener listener) {
		if (titleBar != null) {
			titleBar.setRightButton(resId, listener);
		}
	}

	public void setRight2Button(int resId, OnClickListener listener) {
		if (titleBar != null) {
			titleBar.setRight2Button(resId, listener);
		}
	}

	public void enableBackButton(boolean enable) {
		if (titleBar != null) {
			titleBar.enableBackButton(enable);
		}
	}

	public void setTitle(int resId) {
		setTitle(getString(resId));
	}
	
	private SharedPreferences sharePref;

	public SharedPreferences preferences() {
		if (sharePref == null) {
			sharePref = getActivity().getSharedPreferences(getActivity().getPackageName(),
					Context.MODE_PRIVATE);
		}
		return sharePref;
	}
}
