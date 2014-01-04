package com.cleanchina.weibo;

import java.util.ArrayList;
import java.util.List;

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
import com.cleanchina.bean.NewsListBean;
import com.cleanchina.bean.WeiboBean;
import com.cleanchina.bean.WeiboListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.NetworkImageView;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class WeiboListActivity extends CCActivity implements
		OnItemClickListener, MApiRequestHandler {
	private MApiRequest request;
	private Adapter adapter;
	private String startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = new ListView(this);
		setContentView(list);
		setTitle("微博列表");

		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	protected void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = arg0.getItemAtPosition(position);
		if (item instanceof WeiboBean) {
			// WeiboBean news = (WeiboBean) item;
			// startActivity(new Intent(Intent.ACTION_VIEW,
			// Uri.parse("cleanchina://newsdetail?id=" + news.news_id)));
		}
	}

	private void requestData() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiPost(Constant.DOMAIN + "weibo",
				WeiboListBean.class, "pagesize", "20", "submit_time", startTime);
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		List<WeiboBean> data = new ArrayList<WeiboBean>();
		boolean error = false;
		boolean isEnd = false;

		public void appendData(WeiboBean[] data) {
			for (WeiboBean newsBean : data) {
				this.data.add(newsBean);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return isEnd ? data.size() : data.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position < data.size()) {
				return data.get(position);
			}
			return error ? ERROR : LOADING;
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
				WeiboBean weibo = (WeiboBean) item;
				View view = convertView;
				if (!(view instanceof LinearLayout)) {
					view = LayoutInflater.from(WeiboListActivity.this).inflate(
							R.layout.layout_list_item_weibo, null);
				}
				TextView date = (TextView) view.findViewById(R.id.date);
				date.setText(weibo.weibo_submittime);
				TextView content = (TextView) view.findViewById(R.id.content);
				content.setText(weibo.weibo_content);
				NetworkImageView image = (NetworkImageView) view
						.findViewById(R.id.image);
				if (weibo.images != null && weibo.images.length > 0) {
					image.setVisibility(View.VISIBLE);
					image.setImage(weibo.images[0]);
				} else {
					image.setVisibility(View.GONE);
				}
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
		if (resp.result() instanceof WeiboListBean) {
			WeiboListBean ml = (WeiboListBean) resp.result();
			adapter.appendData(ml.data);
			if (ml.data.length == 0) {
				startTime = ml.data[ml.data.length - 1].weibo_submittime;
			} else {
				adapter.isEnd = true;
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		adapter.error = true;
		adapter.notifyDataSetChanged();
	}
}
