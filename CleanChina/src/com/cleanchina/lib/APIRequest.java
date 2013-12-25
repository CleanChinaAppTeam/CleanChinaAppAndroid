package com.cleanchina.lib;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiFormInputStream;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.impl.BasicMApiRequest;
import com.google.gson.Gson;

public class APIRequest extends BasicMApiRequest {

	public APIRequest(String url, String method, InputStream input,
			CacheType defaultCacheType, Class<?> resultClazz,
			List<NameValuePair> headers) {
		super(url, method, input, defaultCacheType, resultClazz, headers);
	}

	public static MApiRequest mapiGet(String url, CacheType defaultCacheType,
			Class<?> resultClazz, String... forms) {
		Map<String, String> formMap = new HashMap<String, String>();
		formMap.put("identication", new Gson().toJson(basicParams()));
		Map<String, String> data = new HashMap<String, String>();
		for (int i = 0; i < forms.length - 1; i += 2) {
			data.put(forms[i], forms[i + 1]);
		}
		formMap.put("data", new Gson().toJson(data));
		APIRequest r = new APIRequest(appendForms(url, formMap), GET, null,
				defaultCacheType, resultClazz, null);
		return r;
	}

	public static MApiRequest mapiPost(String url, Class<?> resultClazz,
			String... forms) {
		if (forms.length % 2 != 0) {
			throw new IllegalArgumentException("forms missed!");
		}

		List<NameValuePair> formList = new ArrayList<NameValuePair>();
		formList.add(new BasicNameValuePair("identication", new Gson()
				.toJson(basicParams())));
		Map<String, String> data = new HashMap<String, String>();
		for (int i = 0; i < forms.length - 1; i += 2) {
			data.put(forms[i], forms[i + 1]);
		}
		formList.add(new BasicNameValuePair("data", new Gson().toJson(data)));
		APIRequest r = new APIRequest(url, POST, new MApiFormInputStream(
				formList), CacheType.DISABLED, resultClazz, null);
		return r;
	}

	private static Map<String, String> basicParams() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", "basic");
		map.put("password", "test123");
		map.put("username", "admin");
		map.put("imei", "0a97e5810d5177a6");
		map.put("info", "systemModel:Android");
		map.put("version", "1.0");
		return map;
	}

	protected static List<NameValuePair> form(String... keyValues) {
		int n = keyValues.length / 2;
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>(n);
		for (int i = 0; i < n; i++) {
			list.add(new BasicNameValuePair(keyValues[i * 2],
					keyValues[i * 2 + 1]));
		}
		return list;
	}

}
