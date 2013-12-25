package com.cleanchina.meeting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.CompanyBean;
import com.cleanchina.bean.CompanyListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class SearchFragment extends CCFragment implements
		OnCheckedChangeListener, MApiRequestHandler {

	private EditText input;
	private RadioButton cbAZ;
	private RadioButton cbPrd;
	private ListView listView;
	private Adapter adapter;
	private MApiRequest request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meeting_search, null);
		input = (EditText) view.findViewById(R.id.meeting_search_input);
		cbAZ = (RadioButton) view.findViewById(R.id.meeting_search_cb_az);
		cbPrd = (RadioButton) view.findViewById(R.id.meeting_search_cb_product);
		cbAZ.setOnCheckedChangeListener(this);
		cbPrd.setOnCheckedChangeListener(this);
		listView = (ListView) view.findViewById(R.id.list);
		adapter = new Adapter();
		listView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("商展查询");
	}

	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == cbAZ) {

		}
		adapter.reset();
	}

	private void requestData(String key) {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "company",
				CacheType.NORMAL, CompanyListBean.class, "querykey",
				key == null ? "" : key);
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		CompanyBean[] data;
		boolean failed;

		@Override
		public int getCount() {
			return data != null ? data.length : 1;
		}

		@Override
		public Object getItem(int position) {
			if (failed) {
				return ERROR;
			}
			return data != null ? data[position] : LOADING;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object item = getItem(position);
			if (item == LOADING) {
				requestData(null);
				return getLoadingView(parent, convertView);
			} else if (item == ERROR) {
				return getFailedView(Constant.MSG_NET_ERROR,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								reset();
							}
						}, parent, convertView);
			} else {
				CompanyBean company = (CompanyBean) item;
				TextView tv = new TextView(getActivity());
				tv.setPadding(10, 10, 10, 10);
				tv.setText(company.companyname);
				tv.setTag(company);
				return tv;
			}
		}

		public void reset() {
			adapter.data = null;
			adapter.failed = false;
			adapter.notifyDataSetChanged();
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
		if (resp.result() instanceof CompanyListBean) {
			adapter.data = ((CompanyListBean) resp.result()).data;
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		adapter.failed = true;
		adapter.notifyDataSetChanged();
	}

}
