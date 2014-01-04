package com.cleanchina.forum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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

public class ForumTodayFragment extends CCFragment implements
		OnItemClickListener, MApiRequestHandler {

	private MApiRequest request;
	private Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView list = new ListView(getActivity());
		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return list;
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("论坛活动");
		setRightButton(0, null);
		setRight2Button(0, null);
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

	private void requestData() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest
				.mapiGet(Constant.DOMAIN + "forum", CacheType.NORMAL,
						ForumListBean.class, "onlygettodayforum", "1");
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter {

		ForumBean[] data;
		boolean error = false;

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
			ForumListBean ml = (ForumListBean) resp.result();
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
