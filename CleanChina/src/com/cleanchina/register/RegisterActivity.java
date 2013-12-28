package com.cleanchina.register;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.cleanchina.R;
import com.cleanchina.app.CCActivity;
import com.cleanchina.bean.BasicBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.cleanchina.register.view.CityInputView;
import com.cleanchina.register.view.MultiStateInputView;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;

public class RegisterActivity extends CCActivity implements OnClickListener,
		MApiRequestHandler {

	private MultiStateInputView name;
	private MultiStateInputView company;
	private MultiStateInputView address;
	private CityInputView city;
	private MultiStateInputView phone;
	private MultiStateInputView email;

	private View ok;

	private MApiRequest request;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_register);
		setTitle("观众登记");

		name = (MultiStateInputView) findViewById(R.id.register_name);
		company = (MultiStateInputView) findViewById(R.id.register_comp);
		address = (MultiStateInputView) findViewById(R.id.register_address);
		city = (CityInputView) findViewById(R.id.register_city);
		phone = (MultiStateInputView) findViewById(R.id.register_tel);
		phone.setInputType(InputType.TYPE_CLASS_PHONE);
		phone.setInputMaxLength(11);
		email = (MultiStateInputView) findViewById(R.id.register_mail);
		email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		ok = findViewById(R.id.ok);
		ok.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		if (request != null) {
			mapiService().abort(request, this, true);
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v == ok) {
			if (checkInput()) {
				if (request != null) {
					mapiService().abort(request, this, true);
				}
				request = APIRequest.mapiGet(Constant.DOMAIN + "person",
						CacheType.DISABLED, BasicBean.class, "name",
						name.getInput(), "company", company.getInput(),
						"address", address.getInput(), "city", city.getInput()
								+ "省" + city.getInput2() + "市", "mobile",
						phone.getInput(), "email", email.getInput());
				mapiService().exec(request, this);

				showProgressDialog("正在提交，请稍候...");
			}
		}
	}

	private boolean checkInput() {
		if (TextUtils.isEmpty(name.getInput())) {
			name.setError("姓名不能为空");
			return false;
		} else {
			name.setError(null);
		}

		if (TextUtils.isEmpty(company.getInput())) {
			company.setError("公司不能为空");
			return false;
		} else {
			company.setError(null);
		}

		if (TextUtils.isEmpty(address.getInput())) {
			address.setError("地址不能为空");
			return false;
		} else {
			address.setError(null);
		}

		if (TextUtils.isEmpty(city.getInput())) {
			city.setError("省份不能为空");
			return false;
		} else if (TextUtils.isEmpty(city.getInput2())) {
			city.setError2("城市不能为空");
			return false;
		} else {
			city.setError(null);
		}

		if (TextUtils.isEmpty(phone.getInput())) {
			phone.setError("手机不能为空");
			return false;
		} else {
			phone.setError(null);
		}

		if (TextUtils.isEmpty(email.getInput())) {
			email.setError("邮箱不能为空");
			return false;
		} else {
			email.setError(null);
		}

		return true;
	}

	@Override
	public void onRequestStart(MApiRequest req) {
	}

	@Override
	public void onRequestProgress(MApiRequest req, int count, int total) {
	}

	@Override
	public void onRequestFinish(MApiRequest req, MApiResponse resp) {
		dismissDialog();
		if (resp.result() instanceof BasicBean) {
			showDialog("成功", "登记成功！", null);
		}
	}

	@Override
	public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		dismissDialog();
		showDialog("失败", resp.message().getErrorMsg(), null);
	}

}
