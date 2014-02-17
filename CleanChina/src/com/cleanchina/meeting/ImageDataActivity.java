package com.cleanchina.meeting;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.ImageDataBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

/**
 * api return a image for show
 * 
 * @author denny
 * 
 */
public class ImageDataActivity extends CCActivity implements MApiRequestHandler {

	private TextView text;
	private ProgressBar progress;

	private MApiRequest request;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_data);
		Uri uri = getIntent().getData();
		if (uri == null) {
			finish();
			return;
		}
		setTitle(uri.getQueryParameter("title"));
		text = (TextView) findViewById(R.id.text);
		progress = (ProgressBar) findViewById(R.id.progress);

		if (request != null) {
			mapiService().abort(request, this, true);
		}

		request = APIRequest.mapiGet(uri.getQueryParameter("url"),
				CacheType.NORMAL, ImageDataBean.class);
		mapiService().exec(request, this);
	}

	@Override
	protected void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onRequestStart(MApiRequest req) {
	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		progress.setVisibility(View.GONE);
		if (resp.result() instanceof ImageDataBean) {
			ImageDataBean result = (ImageDataBean) resp.result();
			text.setText(result.data.img);
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		progress.setVisibility(View.GONE);
		Toast.makeText(this, Constant.MSG_NET_ERROR, Toast.LENGTH_SHORT).show();
	}

}
