package com.cleanchina.news;

import android.os.Bundle;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.NewsDetailBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.NetworkImageView;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class NewsDetailActivity extends CCActivity implements
		MApiRequestHandler {

	private TextView title;
	private TextView date;
	private NetworkImageView image;
	private TextView content;

	private MApiRequest request;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		setTitle("新闻详情");

		title = (TextView) findViewById(R.id.text);
		date = (TextView) findViewById(R.id.date);
		image = (NetworkImageView) findViewById(R.id.image);
		content = (TextView) findViewById(R.id.content);

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
		showProgressDialog(Constant.MSG_LOADING_DATA);
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "newsdetail",
				CacheType.NORMAL, NewsDetailBean.class, "news_id", id);
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
		if (resp.result() instanceof NewsDetailBean) {
			NewsDetailBean detail = (NewsDetailBean) resp.result();
			if (detail.data != null) {
				title.setText(detail.data.news_title);
				date.setText(detail.data.news_date);
				image.setImage(detail.data.news_img);
				content.setText(detail.data.news_content);
			} else {
				showDialog(getString(R.string.app_name), detail.message, null);
			}
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		dismissDialog();
	}
}
