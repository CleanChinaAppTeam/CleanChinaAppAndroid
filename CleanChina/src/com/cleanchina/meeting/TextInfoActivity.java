package com.cleanchina.meeting;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;

/**
 * show a text
 * 
 * @author denny
 * 
 */
public class TextInfoActivity extends CCActivity {

	private TextView text;
	private ProgressBar progress;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_data);
		Uri uri = getIntent().getData();
		if (uri == null) {
			finish();
			return;
		}
		setTitle(uri.getQueryParameter("title"));
		text = (TextView) findViewById(R.id.text);
		text.setText(getIntent().getStringExtra("text"));
		progress = (ProgressBar) findViewById(R.id.progress);
		progress.setVisibility(View.GONE);

	}

}
