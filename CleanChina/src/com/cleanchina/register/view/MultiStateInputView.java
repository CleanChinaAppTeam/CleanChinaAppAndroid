package com.cleanchina.register.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleanchina.R;

public class MultiStateInputView extends LinearLayout {

	protected EditText input;
	protected ImageView icon;
	protected TextView tips;

	public MultiStateInputView(Context context) {
		this(context, null);
	}

	public MultiStateInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		input = (EditText) findViewById(R.id.multi_state_et);
		icon = (ImageView) findViewById(R.id.multi_state_image);
		tips = (TextView) findViewById(R.id.multi_tips);
	}

	public String getInput() {
		return input.getText().toString().trim();
	}
	
	public void setInputType(int type) {
		input.setInputType(type);
	}
	
	public void setInputMaxLength(int max) {
		InputFilter[] filters = {new LengthFilter(max)}; 
		input.setFilters(filters);
	}

	public void setError(String error) {
		if (error != null) {
			input.setBackgroundResource(R.drawable.bg_edit_error);
			icon.setImageResource(R.drawable.ic_wrong);
		} else {
			input.setBackgroundResource(R.drawable.bg_edit);
			icon.setImageResource(R.drawable.ic_right);
		}
		tips.setText(error);
	}

}
