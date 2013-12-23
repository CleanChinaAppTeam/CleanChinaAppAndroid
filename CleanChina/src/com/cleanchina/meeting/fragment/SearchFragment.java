package com.cleanchina.meeting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;

public class SearchFragment extends CCFragment implements OnCheckedChangeListener{

	private EditText input;
	private RadioButton cbAZ;
	private RadioButton cbPrd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meeting_search, null);
		input = (EditText) view.findViewById(R.id.meeting_search_input);
		cbAZ = (RadioButton) view.findViewById(R.id.meeting_search_cb_az);
		cbPrd = (RadioButton) view.findViewById(R.id.meeting_search_cb_product);
		cbAZ.setOnCheckedChangeListener(this);
		cbPrd.setOnCheckedChangeListener(this);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("商展查询");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == cbAZ) {
			
		}
	}

}
