package com.cleanchina.meeting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;

public class MeetingActivity extends CCActivity implements OnClickListener {

	private View backView;
	private View aboutView;
	private View mapView;
	private View audienceView;
	private View searchView;
	private View awardView;
	private View evenView;
	private View costView;

	@Override
	protected int customTitleType() {
		return Window.FEATURE_NO_TITLE;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_meeting);
		backView = findViewById(R.id.meeting_back);
		aboutView = findViewById(R.id.meeting_about);
		mapView = findViewById(R.id.meeting_map);
		audienceView = findViewById(R.id.meeting_audience);
		searchView = findViewById(R.id.meeting_search);
		awardView = findViewById(R.id.meeting_award);
		evenView = findViewById(R.id.meeting_even);
		costView = findViewById(R.id.meeting_cost);

		backView.setOnClickListener(this);
		aboutView.setOnClickListener(this);
		aboutView.setTag("about");
		mapView.setOnClickListener(this);
		mapView.setTag("map");
		audienceView.setOnClickListener(this);
		searchView.setOnClickListener(this);
		searchView.setTag("search");
		awardView.setOnClickListener(this);
		awardView.setTag("award");
		evenView.setOnClickListener(this);
		costView.setOnClickListener(this);
		costView.setTag("cost");
	}

	@Override
	public void onClick(View v) {
		if (v == backView) {
			finish();
		} else {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://meetingtab?tag=" + v.getTag())));
		}
	}

}
