package com.cleanchina.meeting.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.CompanyBean;
import com.cleanchina.bean.CompanyListBean;
import com.cleanchina.bean.CompanySectionBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class CompanySearchFragment extends CCFragment implements
		MApiRequestHandler, OnChildClickListener, OnCheckedChangeListener,
		TextWatcher {

	private EditText input;
	private RadioGroup group;
	private MApiRequest request;

	private ExpandableListView listView;
	private ProgressBar loading;

	private Adapter adapter;

	private int status;
	private static final int STATUS_AZ = 0;
	private static final int STATUS_PRODUCT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meeting_search, null);
		input = (EditText) view.findViewById(R.id.meeting_search_input);
		input.addTextChangedListener(this);
		group = (RadioGroup) view.findViewById(R.id.meeting_search_group);
		group.setOnCheckedChangeListener(this);
		listView = (ExpandableListView) view.findViewById(R.id.list);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new Adapter();
		listView.setOnChildClickListener(this);
		listView.setAdapter(adapter);

		changeStatus(STATUS_AZ);
		requestData(null, 1);
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("商展查询");
		setRightButton(0, null);
		setRight2Button(0, null);

		// if (status == STATUS_AZ) {
		// changeStatus(STATUS_AZ);
		// } else {
		// changeStatus(STATUS_PRODUCT);
		// }
	}

	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		CompanyBean comp = (CompanyBean) parent.getExpandableListAdapter()
				.getChild(groupPosition, childPosition);
		if (comp != null) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("cleanchina://companydetail?id=" + comp.companyid)));
		}
		return true;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.meeting_search_cb_az) {
			changeStatus(STATUS_AZ);
			requestData(null, 1);
		} else if (checkedId == R.id.meeting_search_cb_product) {
			changeStatus(STATUS_PRODUCT);
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
		request = APIRequest.mapiGet(Constant.DOMAIN + "company",
				CacheType.NORMAL, CompanyListBean.class, "querykey",
				key == null ? "" : key, "sortby", String.valueOf(sort));
		mapiService().exec(request, this);
	}

	class Adapter extends BaseExpandableListAdapter {

		private List<String> tagSections = new ArrayList<String>();
		private Map<String, CompanyBean[]> data = new HashMap<String, CompanyBean[]>();

		public void setData(CompanySectionBean[] sections) {
			tagSections.clear();
			data.clear();

			for (CompanySectionBean section : sections) {
				tagSections.add(section.sectionname);
				data.put(section.sectionname, section.company);
			}

			notifyDataSetChanged();
		}

		public void reset() {
			data.clear();
			tagSections.clear();
			notifyDataSetChanged();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			String section = tagSections.get(groupPosition);
			CompanyBean[] childs = data.get(section);
			return childs[childPosition];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			CompanyBean company = (CompanyBean) getChild(groupPosition,
					childPosition);
			View view = convertView;
			if (view == null || !"company".equals(view.getTag())) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.layout_list_item_text14, parent, false);
			}
			TextView tv = (TextView) view.findViewById(R.id.text);
			tv.setText(company.companyname);
			tv.setTag(company);
			view.setTag("company");
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (tagSections.size() == 0) {
				return 0;
			}
			String section = tagSections.get(groupPosition);
			return data.get(section).length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return tagSections.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return tagSections.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null || !"section".equals(view.getTag())) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.section_view, parent, false);
			}
			TextView tv = (TextView) view.findViewById(R.id.listTextView);
			tv.setText((String) getGroup(groupPosition));
			view.setTag("section");
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
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
			adapter.setData(((CompanyListBean) resp.result()).data);
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				if (listView.isGroupExpanded(i)) {
					listView.collapseGroup(i);
				}
			}
			listView.expandGroup(0);
			listView.setSelection(0);
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
		adapter.reset();
		requestData(s.toString(), status == STATUS_AZ ? 1 : 2);
	}

}
