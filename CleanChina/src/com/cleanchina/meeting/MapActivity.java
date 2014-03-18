package com.cleanchina.meeting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cleanchina.app.CCActivity;

public class MapActivity extends CCActivity {

	private Fragment rootFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(android.R.id.primary);
		setContentView(rootView);

		try {
			rootFragment = (Fragment) getClassLoader().loadClass(
					"com.cleanchina.meeting.fragment.MapFragment")
					.newInstance();
		} catch (Exception e) {
			return;
		}

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.primary, rootFragment);
		ft.commit();
	}
}
