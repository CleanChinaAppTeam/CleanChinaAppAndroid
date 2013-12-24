package com.cleanchina.register.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cleanchina.R;

public class CityInputView extends MultiStateInputView {
	
	private EditText input2;

	public CityInputView(Context context) {
		this(context, null);
	}

	public CityInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		input2 = (EditText) findViewById(R.id.multi_state_et2);
	}

}
