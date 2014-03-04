package com.cleanchina.meeting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cleanchina.R;
import com.cleanchina.app.CCTabActivity;
import com.cleanchina.bean.CostInfoResultBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.meeting.fragment.AboutFragment;
import com.cleanchina.meeting.fragment.CompanySearchFragment;
import com.cleanchina.meeting.fragment.CostFragment;
import com.cleanchina.meeting.fragment.MapFragment;
import com.cleanchina.meeting.fragment.RewardFragment;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class MeetingTabActivity extends CCTabActivity implements
		MApiRequestHandler {

	private MApiRequest request;
	private CostInfoResultBean costInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addTab(setIndicator(tabHost.newTabSpec("about"),
				R.drawable.ic_tab_about, "关于展会"), AboutFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("search"),
				R.drawable.ic_tab_search, "展商查询"), CompanySearchFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("map"), R.drawable.ic_tab_map,
				"展会平面图"), MapFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("award"),
				R.drawable.ic_tab_award, "金钻奖"), RewardFragment.class);
		addTab(setIndicator(tabHost.newTabSpec("cost"), R.drawable.ic_tab_cost,
				"聚划算"), CostFragment.class);

		setCurrentTab(getIntent().getData().getQueryParameter("tag"));

		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiGet(Constant.DOMAIN + "message_jhs",
				CacheType.NORMAL, CostInfoResultBean.class);
		mapiService().exec(request, this);
	}

	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setCurrentTab(intent.getData().getQueryParameter("tag"));
	}

	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equals("cost") && costInfo != null) {
			setTitleButton();

		} else {
			setRightButton(0, null);
			setRight2Button(0, null);
		}
	}

	private void setTitleButton() {
		setRightButton(R.drawable.title_how_buy, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("cleanchina://textinfo?title=如何买"));
				intent.putExtra("text", costInfo.data.howbuy);
				startActivity(intent);
			}
		});
		setRight2Button(R.drawable.title_where_buy, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// setCurrentTab("map");
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("cleanchina://map"));
				intent.putExtra("costinfo", costInfo.data);
				startActivity(intent);
			}
		});
	}

	@Override
	protected int getContentViewResId() {
		return R.layout.activity_tab_meeting;
	}

	@Override
	protected int customTitleType() {
		return Window.FEATURE_CUSTOM_TITLE;
	}

	@Override
	public void onRequestFailed(MApiRequest arg0, MApiResponse arg1) {

	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		if (resp.result() instanceof CostInfoResultBean) {
			costInfo = (CostInfoResultBean) resp.result();
			if ("cost".equals(tabHost.getCurrentTabTag())) {
				setTitleButton();
			}
		}
	}

	@Override
	public void onRequestProgress(MApiRequest arg0, int arg1, int arg2) {
	}

	@Override
	public void onRequestStart(MApiRequest arg0) {
	}

}
