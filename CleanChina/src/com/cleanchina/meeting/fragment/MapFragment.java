package com.cleanchina.meeting.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanchina.R;
import com.cleanchina.app.CCFragment;
import com.cleanchina.bean.CompanyMapListBean;
import com.cleanchina.bean.CompanyPosBean;
import com.cleanchina.bean.CoordinateBean;
import com.cleanchina.bean.CostInfoBean;
import com.cleanchina.bean.MapListBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.meeting.view.CompanyMapView;
import com.cleanchina.meeting.view.CompanyMapView.MapLoadListener;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class MapFragment extends CCFragment implements OnPhotoTapListener,
		MApiRequestHandler, OnClickListener, MapLoadListener {

	private CompanyMapView map;
	private ProgressBar progress;

	private MApiRequest mapReq;
	private MApiRequest posReq;

	private Map<String, List<CompanyPosBean>> cMap = new HashMap<String, List<CompanyPosBean>>();
	private MapListBean maps;

	private int curMap;
	private CostInfoBean costInfo;
	private boolean needShowDialogWhenMapLoaded;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		costInfo = getActivity().getIntent().getParcelableExtra("costinfo");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_company_map, null);
		map = (CompanyMapView) view.findViewById(R.id.image);
		progress = (ProgressBar) view.findViewById(R.id.progress);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		map.setOnPhotoTapListener(this);
		map.setMapLoadListener(this);

		requestMap();
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle("展位平面图");
		setRightButton(R.drawable.title_2, this);
		setRight2Button(R.drawable.title_1, this);
	}

	private void openCurCmpByName(String cmpName) {
		if (!TextUtils.isEmpty(cmpName) && maps != null) {
			List<CompanyPosBean> cpList = cMap
					.get(maps.data[curMap].zhanweiimg_id);
			if (cpList == null) {
				return;
			}

			for (CompanyPosBean companyPosBean : cpList) {
				if (companyPosBean.companyname.equals(cmpName)) {
					showPopupDialog(companyPosBean);
					return;
				}
			}
		}
	}

	private void openCurCmp(CostInfoBean costInfo) {
		if (costInfo != null && maps != null) {
			List<CompanyPosBean> cpList = cMap.get(costInfo.zhanweiimg_id);
			if (cpList == null) {
				return;
			}

			for (CompanyPosBean companyPosBean : cpList) {
				if (companyPosBean.coordinate.equals(costInfo.coordinate)) {
					showPopupDialog(companyPosBean);
					return;
				}
			}

			CompanyPosBean cmp = new CompanyPosBean();
			cmp.companyid = -1;
			cmp.companyname = "聚划算购买点";
			cmp.zhanweiimg_id = costInfo.zhanweiimg_id;
			cmp.coordinate = costInfo.coordinate;
			showPopupDialog(cmp);
		}
	}

	@Override
	public void onMapLoaded() {
		String cmpName = getActivity().getIntent().getData()
				.getQueryParameter("companyname");
		openCurCmpByName(cmpName);
		if (needShowDialogWhenMapLoaded) {
			openCurCmp(costInfo);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_right_btn) {
			curMap = 1;
			updateMap();

		} else if (v.getId() == R.id.title_right_btn2) {
			curMap = 0;
			updateMap();

		}
	}

	private void updateMap() {
		if (maps == null || maps.data == null || maps.data.length < 2) {
			return;
		}

		map.setImage(maps.data[curMap].zhanweiimg_url);
	}

	@Override
	public void onDestroy() {
		if (mapReq != null) {
			mapiService().abort(mapReq, this, true);
		}
		if (posReq != null) {
			mapiService().abort(posReq, this, true);
		}
		super.onDestroy();
	}

	private void requestMap() {
		progress.setVisibility(View.VISIBLE);
		if (mapReq != null) {
			mapiService().abort(mapReq, this, true);
		}

		mapReq = APIRequest.mapiGet(Constant.DOMAIN + "company3",
				CacheType.NORMAL, MapListBean.class);
		mapiService().exec(mapReq, this);
	}

	private void requestPos() {
		progress.setVisibility(View.VISIBLE);
		if (posReq != null) {
			mapiService().abort(posReq, this, true);
		}

		posReq = APIRequest.mapiGet(Constant.DOMAIN + "company2",
				CacheType.NORMAL, CompanyMapListBean.class);
		mapiService().exec(posReq, this);
	}

	@Override
	public void onPhotoTap(View view, float x, float y) {
		checkPosition((int) (x * map.getMapWidth()),
				(int) (y * map.getMapHeight()));

//		Toast.makeText(
//				getActivity(),
//				"x:" + (int) (x * map.getMapWidth()) + " ,y:"
//						+ (int) (y * map.getMapHeight()), Toast.LENGTH_SHORT)
//				.show();
	}

	private boolean checkPosition(int x, int y) {
		List<CompanyPosBean> cpList = cMap.get(maps.data[curMap].zhanweiimg_id);
		if (cpList == null) {
			return false;
		}

		for (CompanyPosBean cp : cpList) {
			CoordinateBean c = cp.coordinate;
			if (x > c.tlx && y > c.tly && x < c.brx && y < c.bry) {
				showPopupDialog(cp);
				return true;
			}
		}
		return false;
	}

	private void showPopupDialog(final CompanyPosBean cp) {
		if (!map.isMapLoaded()) {
			needShowDialogWhenMapLoaded = true;
			return;
		}
		
		final Dialog dialog = new Dialog(getActivity(), R.style.MapPopUpDialog);
		dialog.setContentView(R.layout.dialog_map_popup);
		((TextView) dialog.findViewById(R.id.title)).setText(cp.companyname);
		View view = dialog.findViewById(R.id.dialog);
		if (cp.companyid == -1) {
			dialog.findViewById(R.id.subtitle).setVisibility(View.GONE);
		} else {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("cleanchina://companydetail?id="
									+ cp.companyid)));
					dialog.dismiss();
				}
			});
		}
		dialog.show();

		CoordinateBean c = cp.coordinate;
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.y = map.getPosYInView(c.bry - (c.bry - c.tly) / 2);
		params.x = map.getPosXInView(c.brx - (c.brx - c.tlx) / 2) - 134;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		dialog.getWindow().setAttributes(params);
	}

	@Override
	public void onRequestStart(MApiRequest req) {
	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		if (req == mapReq) {
			if (resp.result() instanceof MapListBean) {
				maps = (MapListBean) resp.result();
				if (costInfo != null) {
					String id1 = maps.data[0].zhanweiimg_id;
					if (id1.equals(costInfo.zhanweiimg_id)) {
						curMap = 0;
					} else {
						curMap = 1;
					}
					updateMap();
				}
				updateMap();
				requestPos();
			}

		} else {
			progress.setVisibility(View.GONE);
			if (resp.result() instanceof CompanyMapListBean) {
				if (maps == null || maps.data == null || maps.data.length < 2) {
					return;
				}
				CompanyMapListBean result = (CompanyMapListBean) resp.result();
				String id1 = maps.data[0].zhanweiimg_id;
				String id2 = maps.data[1].zhanweiimg_id;
				List<CompanyPosBean> cpList1 = cMap.get(id1);
				List<CompanyPosBean> cpList2 = cMap.get(id2);
				if (cpList1 == null) {
					cpList1 = new ArrayList<CompanyPosBean>();
				}
				if (cpList2 == null) {
					cpList2 = new ArrayList<CompanyPosBean>();
				}
				for (CompanyPosBean cp : result.data) {
					if (cp.zhanweiimg_id.equals(id1)) {
						cpList1.add(cp);
					} else if (cp.zhanweiimg_id.equals(id2)) {
						cpList2.add(cp);
					}
				}
				cMap.put(id1, cpList1);
				cMap.put(id2, cpList2);

				if (costInfo != null) {
					openCurCmp(costInfo);
				}
			}
		}

	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		progress.setVisibility(View.GONE);
		Toast.makeText(getActivity(), resp.message().getErrorMsg(),
				Toast.LENGTH_SHORT).show();
	}

}
