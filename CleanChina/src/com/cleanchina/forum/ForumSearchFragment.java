package com.cleanchina.forum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.ForumBean;
import com.cleanchina.bean.ForumListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.sectionlist.SectionListItem;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class ForumSearchFragment extends CCFragment implements
		MApiRequestHandler, OnItemClickListener, OnCheckedChangeListener,
		TextWatcher {

	private EditText input;
	private RadioGroup group;
	private MApiRequest request;

	private ListView listView;
	private ProgressBar loading;

	private Adapter adapter;

	private int status;
	private static final int STATUS_DATE = 0;
	private static final int STATUS_CLASS = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_forum_search, null);
		input = (EditText) view.findViewById(R.id.meeting_search_input);
		input.addTextChangedListener(this);
		group = (RadioGroup) view.findViewById(R.id.meeting_search_group);
		group.setOnCheckedChangeListener(this);
		listView = (ListView) view.findViewById(R.id.list);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new Adapter();
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);

		changeStatus(STATUS_DATE);
		requestData(null, 1);
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("论坛活动");
		setRightButton(0, null);
		setRight2Button(0, null);

		if (status == STATUS_DATE) {
			changeStatus(STATUS_DATE);
		} else {
			changeStatus(STATUS_CLASS);
		}
	}

	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		Object item = av.getItemAtPosition(position);
		ForumBean forum = null;
		if (item instanceof ForumBean) {
			forum = (ForumBean) item;
		} else if (item instanceof SectionListItem) {
			forum = (ForumBean) ((SectionListItem) item).item;
		}

		if (forum != null) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://forumdetail?id=" + forum.forum_id)));
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.meeting_search_cb_az) {
			changeStatus(STATUS_DATE);
			requestData(null, 1);
		} else {
			changeStatus(STATUS_CLASS);
			requestData(null, 2);
		}
	}

	private void changeStatus(int status) {
		this.status = status;
	}

	private void requestData(String key, int sort) {
		loading.setVisibility(View.VISIBLE);
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "forum",
				CacheType.NORMAL, ForumListBean.class, params(key, sort));
		mapiService().exec(request, this);
	}

	private String[] params(String key, int type) {
		if (type == 1) {
			String[] result = { "querykey", key == null ? "" : key, "timesort",
					"1", "timesorttype", "1" };
			return result;
		} else {
			String[] result = { "querykey", key == null ? "" : key,
					"classsort", "1", "classsorttype", "1" };
			return result;
		}
	}

	class Adapter extends BasicAdapter {

		ForumBean[] data;

		public void setData(ForumBean[] data) {
			this.data = data;
			notifyDataSetChanged();
		}

		public void reset() {
			data = null;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ForumBean forum = (ForumBean) getItem(position);
			View view = convertView;
			if (view == null || !"company".equals(view.getTag())) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.layout_list_item_text14, parent, false);
			}
			TextView tv = (TextView) view.findViewById(R.id.text);
			tv.setText(forum.forum_name);
			tv.setTag(forum);
			view.setTag("company");
			return view;
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
		if (resp.result() instanceof ForumListBean) {
			adapter.setData(((ForumListBean) resp.result()).data);
		}
		loading.setVisibility(View.GONE);
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		adapter.notifyDataSetChanged();
		loading.setVisibility(View.GONE);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		requestData(s.toString(), status == STATUS_DATE ? 1 : 2);
	}

}
