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

	public void setError2(String error) {
		super.setError(null);
		if (error != null) {
			input2.setBackgroundResource(R.drawable.bg_edit_error);
			icon.setImageResource(R.drawable.ic_wrong);
		} else {
			input2.setBackgroundResource(R.drawable.bg_edit);
			icon.setImageResource(R.drawable.ic_right);
		}
		tips.setText(error);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		input2 = (EditText) findViewById(R.id.multi_state_et2);
	}

	public String getInput2() {
		return input2.getText().toString().trim();
	}

}
