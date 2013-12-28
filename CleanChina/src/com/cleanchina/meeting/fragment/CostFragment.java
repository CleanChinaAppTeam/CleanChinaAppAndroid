package com.cleanchina.meeting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;

public class CostFragment extends CCFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cost, null);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setTitle("聚划算");
	}

}
