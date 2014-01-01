package com.cleanchina.ebook;

import uk.co.senab.photoview.PhotoView;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.EbookBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.widget.HackyViewPager;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class EbookActivity extends CCActivity implements MApiRequestHandler {

	private ViewPager mViewPager;
	private ProgressBar progress;

	private MApiRequest request;
	private String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ebook);
		setTitle("电子杂志");
		
		id = getIntent().getData().getQueryParameter("id");
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		progress = (ProgressBar) findViewById(R.id.progress);

		mViewPager.setAdapter(new SamplePagerAdapter());
		requestData();
	}

	@Override
	protected void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	private void requestData() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "magazine",
				CacheType.NORMAL, EbookBean.class, "magazine_id");
		mapiService().exec(request, this);
	}

	static class SamplePagerAdapter extends PagerAdapter {

		private String[] images;

		public void setDate(String[] images) {
			this.images = images;
		}

		@Override
		public int getCount() {
			return images == null ? 0 : images.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			photoView.setImage(images[position]);
			photoView.setBackgroundColor(container.getContext().getResources()
					.getColor(android.R.color.black));

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onRequestStart(MApiRequest req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		// TODO Auto-generated method stub

	}
}
