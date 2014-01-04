package com.cleanchina.basic;

import java.net.URLDecoder;

import com.cleanchina.app.CCWebActivity;
import com.cleanchina.app.CCWebActivity.DPWebViewClient;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends CCWebActivity {
	private String url;
	private String title;
	private boolean openexternal;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (getIntent().getData() != null) {
			url = getIntent().getData().getQueryParameter("url");
			title = getIntent().getData().getQueryParameter("title");
			String oe = getIntent().getData().getQueryParameter("openexternal");
			openexternal = "1".equals(oe) || "true".equals(oe);
			if (url != null)
				url = URLDecoder.decode(url);
		} else {
			url = getIntent().getStringExtra("url");
			title = getIntent().getStringExtra("title");
			openexternal = getIntent().getBooleanExtra("openExternal", false);
		}

		if (url == null)
			finish();

		webView.loadUrl(url);
		setTitle(title);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP
				&& webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected WebViewClient createWebViewClient() {
		return new MyWebViewClient();
	}

	public class MyWebViewClient extends DPWebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (openexternal) {
				if (url.equals(WebActivity.this.url))
					return false;
				openExternalUrl(url);
				return true;
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
	}
}