package com.cleanchina.forum;

import android.os.Bundle;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.ForumBean;
import com.cleanchina.bean.ForumDetailBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class ForumDetailActivity extends CCActivity implements
		MApiRequestHandler {

	private TextView nameView;
	private TextView timeView;
	private TextView addressView;
	private TextView instructorView;
	private TextView agendaView;
	private TextView summaryView;

	private MApiRequest request;
	public String id;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_forum_detail);
		setTitle("论坛介绍");
		nameView = (TextView) findViewById(R.id.detail_name);
		timeView = (TextView) findViewById(R.id.detail_time);
		addressView = (TextView) findViewById(R.id.detail_adress);
		instructorView = (TextView) findViewById(R.id.detail_instructor);
		agendaView = (TextView) findViewById(R.id.detail_agenda);
		summaryView = (TextView) findViewById(R.id.detail_summary);

		id = getIntent().getData().getQueryParameter("id");
		requestData();
	}

	@Override
	protected void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	private void requestData() {
		showProgressDialog("加载中...");
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "forum1",
				CacheType.NORMAL, ForumDetailBean.class, "forum_id", id);
		mapiService().exec(request, this);
	}

	@Override
	public void onRequestStart(MApiRequest req) {
	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		dismissDialog();
		if (resp.result() instanceof ForumDetailBean) {
			ForumBean result = ((ForumDetailBean) resp.result()).data;
			nameView.setText(result.forum_name);
			timeView.setText(result.forum_date);
			addressView.setText(result.forum_address);
			instructorView.setText(result.instructor);
			agendaView.setText(result.agenda);
			summaryView.setText(result.summary);
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		dismissDialog();
		showDialog(null, resp.message().getErrorMsg(), null);
	}

}
