package com.cleanchina.meeting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.DtailBean;
import com.cleanchina.bean.DetailResultBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.NetworkImageView;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class MeetingDetailActivity extends CCActivity implements
		MApiRequestHandler {

	private NetworkImageView iconView;
	private TextView numView;
	private TextView nameView;
	private TextView timeView;
	private TextView addressView;
	private TextView siteView;
	private TextView summaryView;

	private MApiRequest request;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_detail);
		setTitle("展会介绍");
		iconView = (NetworkImageView) findViewById(R.id.detail_icon);
		numView = (TextView) findViewById(R.id.detail_num);
		numView.setVisibility(View.INVISIBLE);
		nameView = (TextView) findViewById(R.id.detail_name);
		timeView = (TextView) findViewById(R.id.detail_time);
		addressView = (TextView) findViewById(R.id.detail_adress);
		siteView = (TextView) findViewById(R.id.detail_site);
		summaryView = (TextView) findViewById(R.id.detail_summary);

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
		request = APIRequest.mapiGet(Constant.DOMAIN + "expo",
				CacheType.NORMAL, DetailResultBean.class);
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
		if (resp.result() instanceof DetailResultBean) {
			DtailBean result = ((DetailResultBean) resp.result()).data;
			iconView.setImage(result.logo);
			nameView.setText(result.name);
			timeView.setText("日期：" + result.date);
			addressView.setText("地址：" + result.address);
			siteView.setText("网址：" + result.website);
			summaryView.setText("简介：" + result.summary);
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		dismissDialog();
		showDialog(null, resp.message().getErrorMsg(), null);
	}

}
