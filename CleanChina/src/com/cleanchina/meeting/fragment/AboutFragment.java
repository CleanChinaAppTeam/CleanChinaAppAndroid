package com.cleanchina.meeting.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.lib.Constant;
import com.dennytech.common.adapter.BasicAdapter;

public class AboutFragment extends CCFragment implements OnItemClickListener {

	private ListView listView;
	private Adapter adapter;
	private static final String[] menus = { "展会介绍", "如何参观", "酒店住宿", "免费班车",
			"停车服务", "现场服务" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_listview, null);
		listView = (ListView) v.findViewById(R.id.list);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new Adapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("关于展会");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (position) {
		case 0:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://detail")));
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://imagedata?title=" + menus[position]
							+ "&url=" + Constant.DOMAIN + "expo" + position)));
			break;

		default:
			break;
		}

	}

	class Adapter extends BasicAdapter {

		@Override
		public int getCount() {
			return menus.length;
		}

		@Override
		public String getItem(int position) {
			return menus[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(getActivity()).inflate(
						R.layout.list_item_meeting_about, null);
			}
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(getItem(position));
			return view;
		}

	}

}
