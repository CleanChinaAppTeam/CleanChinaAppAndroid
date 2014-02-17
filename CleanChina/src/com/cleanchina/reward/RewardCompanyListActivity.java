package com.cleanchina.reward;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.RewardCompanyBean;
import com.cleanchina.bean.RewardCompanyListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class RewardCompanyListActivity extends CCActivity implements
		OnItemClickListener, MApiRequestHandler {

	private MApiRequest request;
	private Adapter adapter;
	private String id;
	private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = new ListView(this);
		setContentView(list);

		id = getIntent().getData().getQueryParameter("id");
		title = getIntent().getData().getQueryParameter("title");

		setTitle(title);
		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = arg0.getItemAtPosition(position);
		if (item instanceof RewardCompanyBean) {
			RewardCompanyBean company = (RewardCompanyBean) item;
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://companydetail?id=" + company.companyid)));
		}
	}

	private void requestData() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiPost(Constant.DOMAIN + "reward",
				RewardCompanyListBean.class, "rewardid", id);
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		RewardCompanyBean[] data;
		boolean error = false;

		public void setData(RewardCompanyBean[] data) {
			this.data = data;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (data == null || error) {
				return 1;
			}
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			if (error) {
				return ERROR;
			}
			return data == null ? LOADING : data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object item = getItem(position);
			if (item == LOADING) {
				requestData();
				return getLoadingView(parent, convertView);
			} else if (item == ERROR) {
				return getFailedView("服务器错误", new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestData();
					}
				}, parent, convertView);
			} else {
				RewardCompanyBean company = (RewardCompanyBean) item;
				View view = convertView;
				if (!(view instanceof LinearLayout)) {
					view = LayoutInflater.from(RewardCompanyListActivity.this)
							.inflate(R.layout.layout_list_item_text16, null);
				}
				TextView text = (TextView) view.findViewById(R.id.text);
				text.setText(company.companyname);
				return view;
			}
		}

	}

	@Override
	public void onRequestStart(MApiRequest req) {
	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		if (resp.result() instanceof RewardCompanyListBean) {
			RewardCompanyListBean ml = (RewardCompanyListBean) resp.result();
			adapter.data = ml.data;
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		adapter.error = true;
		adapter.notifyDataSetChanged();
	}

}
