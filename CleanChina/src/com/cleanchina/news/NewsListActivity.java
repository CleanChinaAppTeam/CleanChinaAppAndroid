package com.cleanchina.news;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.cleanchina.bean.NewsBean;
import com.cleanchina.bean.NewsListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.NetworkImageView;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class NewsListActivity extends CCActivity implements
		OnItemClickListener, MApiRequestHandler {

	private MApiRequest request;
	private Adapter adapter;
	private int startNewsId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = new ListView(this);
		setContentView(list);
		setTitle("新闻列表");

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
		if (item instanceof NewsBean) {
			NewsBean news = (NewsBean) item;
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://newsdetail?id=" + news.news_id)));
		}
	}

	private void requestData() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiPost(Constant.DOMAIN + "news",
				NewsListBean.class, "pagesize", "20", "lessthan_newsid",
				String.valueOf(startNewsId));
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		List<NewsBean> data = new ArrayList<NewsBean>();
		boolean error = false;
		boolean isEnd = false;

		public void appendData(NewsBean[] data) {
			for (NewsBean newsBean : data) {
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
				NewsBean news = (NewsBean) item;
				View view = convertView;
				if (!(view instanceof LinearLayout)) {
					view = LayoutInflater.from(NewsListActivity.this).inflate(
							R.layout.layout_list_item_news, null);
				}
				NetworkImageView image = (NetworkImageView) view.findViewById(R.id.icon);
				if (TextUtils.isEmpty(news.news_img)) {
					image.setVisibility(View.GONE);
				} else {
					image.setVisibility(View.VISIBLE);
					image.setImage(news.news_img);
				}
				TextView title = (TextView) view.findViewById(R.id.text);
				title.setText(news.news_title);
				TextView date = (TextView) view.findViewById(R.id.date);
				date.setText(news.news_date);
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
		if (resp.result() instanceof NewsListBean) {
			NewsListBean ml = (NewsListBean) resp.result();
			adapter.appendData(ml.data);
			if (ml.data.length > 0) {
				startNewsId = ml.data[ml.data.length - 1].news_id;
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
