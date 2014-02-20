package com.cleanchina;

import java.net.URLEncoder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.MessageResultBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.loading.AdvertiseManager;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class HomeActivity extends CCActivity implements OnClickListener,
		MApiRequestHandler {

	private View newsBtn;
	private View marketBtn;
	private View meetingBtn;
	private View ebookBtn;
	private View youkuBtn;
	private View wechatBtn;
	private View weiboBtn;

	private View msgLayout;
	private TextView msgTv;
	private View msgCloseBtn;

	private AdvertiseManager manager;

	private MApiRequest request;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_home);
		newsBtn = findViewById(R.id.home_news);
		marketBtn = findViewById(R.id.home_market);
		meetingBtn = findViewById(R.id.home_meeting);
		ebookBtn = findViewById(R.id.home_ebook);
		youkuBtn = findViewById(R.id.home_youku);
		wechatBtn = findViewById(R.id.home_wechat);
		weiboBtn = findViewById(R.id.home_weibo);

		newsBtn.setOnClickListener(this);
		marketBtn.setOnClickListener(this);
		meetingBtn.setOnClickListener(this);
		ebookBtn.setOnClickListener(this);
		youkuBtn.setOnClickListener(this);
		wechatBtn.setOnClickListener(this);
		weiboBtn.setOnClickListener(this);

		msgLayout = findViewById(R.id.message);
		msgTv = (TextView) findViewById(R.id.message_content);
		msgCloseBtn = findViewById(R.id.message_close);
		msgCloseBtn.setOnClickListener(this);

		manager = new AdvertiseManager(this, mapiService(), imageService());
		manager.start();

		requestMessage();
	}

	private void requestMessage() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		request = APIRequest.mapiGet(Constant.DOMAIN + "message",
				CacheType.DISABLED, MessageResultBean.class);
		mapiService().exec(request, this);
	}

	@Override
	protected void onDestroy() {
		manager.stop();
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	protected int customTitleType() {
		return Window.FEATURE_NO_TITLE;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.home_meeting) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://meeting")));
		} else if (v.getId() == R.id.home_news) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://newslist")));

		} else if (v.getId() == R.id.home_market) {
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("cleanchina://web?url="
								+ URLEncoder.encode(
										"http://cn.clean-china.com/", "UTF-8"))));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (v.getId() == R.id.home_ebook) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://ebooklist")));
		} else if (v.getId() == R.id.home_youku) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("cleanchina://weibolist")));
		} else if (v.getId() == R.id.message_close) {
			preferences().edit().putString("messageid", (String) v.getTag())
					.commit();
			msgLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRequestFailed(MApiRequest arg0, MApiResponse arg1) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		if (resp.result() instanceof MessageResultBean) {
			MessageResultBean result = (MessageResultBean) resp.result();
			if (result.data == null
					|| TextUtils.isEmpty(result.data.msg_content)) {
				return;
			}
			
			String existMsgID = preferences().getString("messageid", "");
			if (existMsgID.equals(result.data.msg_id)) {
				return;
			}
			
			msgTv.setText(result.data.msg_content);
			msgCloseBtn.setTag(result.data.msg_id);
			msgLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onRequestProgress(MApiRequest arg0, int arg1, int arg2) {
	}

	@Override
	public void onRequestStart(MApiRequest arg0) {
	}

}
