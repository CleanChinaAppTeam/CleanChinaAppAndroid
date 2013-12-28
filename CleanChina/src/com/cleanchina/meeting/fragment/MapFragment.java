package com.cleanchina.meeting.fragment;

import android.os.Bundle;

import com.cleanchina.app.CCFragment;

public class MapFragment extends CCFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setTitle("展位平面图");
		setRightButton(0, null);
		setRight2Button(0, null);
	}

}
