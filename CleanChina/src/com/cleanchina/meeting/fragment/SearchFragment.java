package com.cleanchina.meeting.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.AlphabetBar;
import com.cleanchina.widget.sectionlist.SectionListAdapter;
import com.cleanchina.widget.sectionlist.SectionListItem;
import com.cleanchina.widget.sectionlist.SectionListView;
import com.dennytech.common.adapter.BasicAdapter;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class SearchFragment extends CCFragment implements MApiRequestHandler,
		OnItemClickListener, OnCheckedChangeListener, TextWatcher {

	private EditText input;
	private RadioGroup group;
	private MApiRequest request;

	private AlphabetBar mIndexBar;
	private SectionListView listView;
	private ListView listView2;
	private ProgressBar loading;

	private SectionListAdapter mSectionAdapter;
	private Adapter adapter;
	private Adapter adapter2;

	private int status;
	private static final int STATUS_AZ = 0;
	private static final int STATUS_PRODUCT = 1;
	private static final int STATUS_SEARCH = 2;

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
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					changeStatus(STATUS_SEARCH);
				}
			}
		});
		group = (RadioGroup) view.findViewById(R.id.meeting_search_group);
		group.setOnCheckedChangeListener(this);
		listView = (SectionListView) view.findViewById(R.id.list);
		mIndexBar = (AlphabetBar) view.findViewById(R.id.sidebar);
		listView2 = (ListView) view.findViewById(R.id.list2);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new Adapter();
		mSectionAdapter = new SectionListAdapter(getActivity()
				.getLayoutInflater(), adapter);
		mSectionAdapter.setOnItemClickListener(this);
		listView.setOnItemClickListener(mSectionAdapter);
		mIndexBar.setListView(listView);

		adapter2 = new Adapter();
		listView2.setAdapter(adapter2);
		listView2.setOnItemClickListener(this);

		requestData(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("商展查询");
		changeStatus(STATUS_AZ);
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
		CompanyBean comp;
		if (item instanceof CompanyBean) {
			comp = (CompanyBean) item;
		} else if (item instanceof SectionListItem) {
			comp = (CompanyBean) ((SectionListItem) item).item;
		}

		// TODO

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.meeting_search_cb_az) {
			changeStatus(STATUS_AZ);
		} else {
			changeStatus(STATUS_PRODUCT);
		}
	}

	private void changeStatus(int status) {
		this.status = status;

		if (status == STATUS_AZ || status == STATUS_SEARCH) {
			listView2.setVisibility(View.INVISIBLE);
			listView.showTransparentView(true);

		} else if (status == STATUS_PRODUCT) {
			listView2.setVisibility(View.VISIBLE);
			listView.showTransparentView(false);
		}
	}

	private void requestData(String key) {
		loading.setVisibility(View.VISIBLE);
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "company",
				CacheType.NORMAL, CompanyListBean.class, "querykey",
				key == null ? "" : key);
		mapiService().exec(request, this);
	}

	class Adapter extends BasicAdapter implements SectionIndexer {

		private List<SectionListItem> data = new ArrayList<SectionListItem>();
		private List<String> sections = new ArrayList<String>();

		public void setData(CompanyBean[] tagSections) {
			for (CompanyBean comp : tagSections) {
				String sectionName = comp.firstchar;
				if (sectionName != null) {
					boolean exist = false;
					for (int i = 0; i < sections.size(); i++) {
						String s = sections.get(i);
						if (s.equals(sectionName)) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						sections.add(sectionName);
					}
				}

				SectionListItem item = new SectionListItem(comp, sectionName);
				data.add(item);
			}

			Collections.sort(sections);
			Collections.sort(data, new Comparator<SectionListItem>() {

				@Override
				public int compare(SectionListItem lhs, SectionListItem rhs) {
					return ((CompanyBean) (lhs.item)).firstchar
							.compareTo(((CompanyBean) (rhs.item)).firstchar);
				}
			});

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public SectionListItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.layout_list_item_text14, parent, false);
			}

			SectionListItem item = getItem(position);
			CompanyBean company = (CompanyBean) item.item;

			TextView tv = (TextView) view.findViewById(R.id.text);
			tv.setText(company.companyname);
			tv.setTag(company);

			return view;
		}

		public void reset() {
			data.clear();
			sections.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				if (sections.get(section).equals(getItem(i).section)) {
					return i + section;
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			SectionListItem item = getItem(position);
			for (int i = 0; i < sections.size(); i++) {
				if (sections.get(i).equals(item.section)) {
					return i;
				}
			}
			return 0;
		}

		@Override
		public Object[] getSections() {
			String[] sectionArray = new String[sections.size()];
			sections.toArray(sectionArray);
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
			if (status != STATUS_SEARCH) {
				adapter.setData(((CompanyListBean) resp.result()).data);
				mIndexBar.setSectionIndexter(adapter);
				listView.setAdapter(mSectionAdapter);
			}
			adapter2.setData(((CompanyListBean) resp.result()).data);
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
		changeStatus(STATUS_SEARCH);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		adapter2.reset();
		requestData(s.toString());
	}

}
