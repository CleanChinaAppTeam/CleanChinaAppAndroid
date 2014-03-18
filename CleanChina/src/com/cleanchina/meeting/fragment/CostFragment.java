package com.cleanchina.meeting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.CostInfoResultBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class CostFragment extends CCFragment implements MApiRequestHandler{
	
	private MApiRequest request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		
		request = APIRequest.mapiGet(Constant.DOMAIN + "message_jhs",
				CacheType.NORMAL, CostInfoResultBean.class);
		mapiService().exec(request, this);
	}
	
	@Override
	public void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cost, null);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("聚划算");
	}

	@Override
	public void onRequestFailed(MApiRequest arg0, MApiResponse arg1) {
		
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		if (resp.result() instanceof CostInfoResultBean) {
			
		}
	}

	@Override
	public void onRequestProgress(MApiRequest arg0, int arg1, int arg2) {
	}

	@Override
	public void onRequestStart(MApiRequest arg0) {
	}

}
