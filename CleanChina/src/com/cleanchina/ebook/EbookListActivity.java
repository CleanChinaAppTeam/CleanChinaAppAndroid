package com.cleanchina.ebook;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
import com.cleanchina.bean.MagazineBean;
import com.cleanchina.bean.MagazineListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class EbookListActivity extends CCActivity implements
		OnItemClickListener, MApiRequestHandler {

	private MApiRequest request;
	private Adapter adapter;
	private int year;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = new ListView(this);
		setContentView(list);
		setTitle("杂志列表");

		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
		year = Integer.valueOf(format.format(new java.util.Date()));
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
		if (item instanceof MagazineBean) {
			MagazineBean magazine = (MagazineBean) item;
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://ebookdetail?id="
							+ magazine.magazine_id)));
		}
	}

	private void requestData(int year) {
		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiPost(Constant.DOMAIN + "magazine1",
				MagazineListBean.class, "magazine_year", String.valueOf(year));
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		MagazineBean[] data;
		boolean error = false;

		public void setData(MagazineBean[] data) {
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
				requestData(year);
				return getLoadingView(parent, convertView);
			} else if (item == ERROR) {
				return getFailedView("服务器错误", new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestData(year);
					}
				}, parent, convertView);
			} else {
				MagazineBean magazine = (MagazineBean) item;
				View view = convertView;
				if (!(view instanceof LinearLayout)) {
					view = LayoutInflater.from(EbookListActivity.this).inflate(
							R.layout.layout_list_item_text16, null);
				}
				TextView text = (TextView) view.findViewById(R.id.text);
				text.setText(magazine.year + "年" + magazine.period);
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
		if (resp.result() instanceof MagazineListBean) {
			MagazineListBean ml = (MagazineListBean) resp.result();
			if (ml.data == null || ml.data.length == 0) {
				requestData(--year);
			} else {
				adapter.data = ml.data;
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		adapter.error = true;
		adapter.notifyDataSetChanged();
	}
}
