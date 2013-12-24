package com.cleanchina.register.view;

import com.cleanchina.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MultiStateInputView extends LinearLayout {

	protected EditText input;
	protected ImageView icon;

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
	}

	public void setState(boolean wrong) {
		if (wrong) {
			input.setBackgroundResource(R.drawable.bg_edit);
			icon.setImageResource(R.drawable.ic_wrong);
		} else {
			input.setBackgroundResource(R.drawable.bg_edit_error);
			icon.setImageResource(R.drawable.ic_right);
		}
	}

}
