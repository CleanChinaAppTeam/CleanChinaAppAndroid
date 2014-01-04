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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.CompanyBean;
import com.cleanchina.bean.CompanyListBean;
import com.cleanchina.bean.CompanySectionBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.AlphabetBar;
import com.cleanchina.widget.sectionlist.SectionListItem;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class CompanySearchFragment extends CCFragment implements
		MApiRequestHandler, OnItemClickListener, OnCheckedChangeListener,
		TextWatcher {

	private EditText input;
	private RadioGroup group;
	private MApiRequest request;

	private AlphabetBar mIndexBar;
	private ListView listView;
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
		listView = (ListView) view.findViewById(R.id.list);
		mIndexBar = (AlphabetBar) view.findViewById(R.id.sidebar);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new Adapter();
		listView.setOnItemClickListener(this);
		mIndexBar.setListView(listView);
		listView.setAdapter(adapter);
		mIndexBar.setSectionIndexter(adapter);

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
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		Object item = av.getItemAtPosition(position);
		CompanyBean comp = null;
		if (item instanceof CompanyBean) {
			comp = (CompanyBean) item;
		} else if (item instanceof SectionListItem) {
			comp = (CompanyBean) ((SectionListItem) item).item;
		}

		if (comp != null) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("cleanchina://companydetail?id=" + comp.companyid)));
		}
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

	class Adapter extends BasicAdapter implements SectionIndexer {

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

		@Override
		public int getCount() {
			int count = 0;
			for (String section : tagSections) {
				count += (data.get(section).length + 1);
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			int total = 0;
			for (int i = 0; i < tagSections.size(); i++) {
				String section = tagSections.get(i);
				CompanyBean[] cmps = data.get(section);
				total += (cmps.length + 1);
				if (total > position) {
					int pos = total - position;
					if (pos > cmps.length) {
						return section;
					} else {
						return cmps[cmps.length - pos];
					}
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object item = getItem(position);
			if (item instanceof String) {
				View view = convertView;
				if (view == null || !"section".equals(view.getTag())) {
					view = LayoutInflater.from(parent.getContext()).inflate(
							R.layout.section_view, parent, false);
				}
				TextView tv = (TextView) view.findViewById(R.id.listTextView);
				tv.setText((String) item);
				view.setTag("section");
				return view;

			} else {
				CompanyBean company = (CompanyBean) item;
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
		}

		public void reset() {
			data.clear();
			tagSections.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getPositionForSection(int section) {
			int total = 0;
			for (int i = 0; i < section; i++) {
				String s = tagSections.get(i);
				total += (data.get(s).length + 1);
			}
			return total;
		}

		@Override
		public int getSectionForPosition(int position) {
			int i;
			int total = 0;
			for (i = 0; i < tagSections.size(); i++) {
				String section = tagSections.get(i);
				total += (data.get(section).length + 1);
				if (total > position) {
					break;
				}
			}
			return i;
		}

		@Override
		public Object[] getSections() {
			String[] sectionArray = new String[tagSections.size()];
			tagSections.toArray(sectionArray);
			return sectionArray;
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
			mIndexBar.setSectionIndexter(adapter);
			mIndexBar.notifyDataSetChanged();
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
