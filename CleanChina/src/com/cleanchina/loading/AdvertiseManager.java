package com.cleanchina.loading;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.cleanchina.bean.AdvertiseBean;
import com.cleanchina.lib.APIRequest;
import com.cleanchina.lib.Constant;
import com.dennytech.common.service.dataservice.Request;
import com.dennytech.common.service.dataservice.RequestHandler;
import com.dennytech.common.service.dataservice.Response;
import com.dennytech.common.service.dataservice.image.ImageService;
import com.dennytech.common.service.dataservice.image.impl.ImageRequest;
import com.dennytech.common.service.dataservice.mapi.CacheType;
import com.dennytech.common.service.dataservice.mapi.MApiRequest;
import com.dennytech.common.service.dataservice.mapi.MApiRequestHandler;
import com.dennytech.common.service.dataservice.mapi.MApiResponse;
import com.dennytech.common.service.dataservice.mapi.MApiService;

public class AdvertiseManager {

	private Context context;
	private MApiService mapi;
	private ImageService image;

	private MApiRequest mapiReq;
	private ImageRequest imageReq;

	private static final String CACHE_NAME = "advertise_cache.jpg";
	private static final String TEMP_NAME = "advertise_temp.jpg";

	public AdvertiseManager(Context ctx, MApiService mapi, ImageService image) {
		this.context = ctx;
		this.mapi = mapi;
		this.image = image;
	}

	public void start() {
		if (mapiReq != null) {
			mapi.abort(mapiReq, mapiHandler, true);
		}

		mapiReq = APIRequest.mapiGet(Constant.DOMAIN + "advertise",
				CacheType.DISABLED, AdvertiseBean.class);
		mapi.exec(mapiReq, mapiHandler);
	}

	public void stop() {
		if (mapiReq != null) {
			mapi.abort(mapiReq, mapiHandler, true);
		}
		if (imageReq != null) {
			image.abort(imageReq, imageHandler, true);
		}
	}
	
	public boolean existsAdvertise() {
		File cache = context.getCacheDir();
		File adFile = new File(cache, CACHE_NAME);
		return adFile.exists();
	}

	public Bitmap read() {
		File cache = context.getCacheDir();
		File adFile = new File(cache, CACHE_NAME);
		if (adFile.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(adFile);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				fis.close();
				return bitmap;
			} catch (Exception e) {
				try {
					fis.close();
				} catch (IOException e1) {
				}
				return null;
			}
		}
		return null;
	}

	private void save(Bitmap bmp) {
		File cache = context.getCacheDir();
		File adFile = new File(cache, CACHE_NAME);
		File temp = new File(cache, TEMP_NAME);
		
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(
					new FileOutputStream(temp));
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			
			if (adFile.exists()) {
				adFile.delete();
			}
			temp.renameTo(adFile);
			
		} catch (Exception e) {
			try {
				bos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	MApiRequestHandler mapiHandler = new MApiRequestHandler() {

		@Override
		public void onRequestStart(MApiRequest req) {
		}

		@Override
		public void onRequestProgress(MApiRequest req, int count, int total) {
		}

		@Override
		public void onRequestFinish(MApiRequest req, MApiResponse resp) {
			if (resp.result() instanceof AdvertiseBean) {
				AdvertiseBean advertise = (AdvertiseBean) resp.result();
				if (advertise.data != null
						&& !TextUtils.isEmpty(advertise.data.img)) {
					if (imageReq != null) {
						image.abort(imageReq, imageHandler, true);
					}

					imageReq = new ImageRequest(advertise.data.img,
							ImageRequest.TYPE_PHOTO);
					image.exec(imageReq, imageHandler);
				}
			}
		}

		@Override
		public void onRequestFailed(MApiRequest req, MApiResponse resp) {
		}
	};

	RequestHandler<Request, Response> imageHandler = new RequestHandler<Request, Response>() {

		@Override
		public void onRequestStart(Request req) {
		}

		@Override
		public void onRequestProgress(Request req, int count, int total) {
		}

		@Override
		public void onRequestFinish(Request req, Response resp) {
			Bitmap bmp = (Bitmap) resp.result();
			save(bmp);
		}

		@Override
		public void onRequestFailed(Request req, Response resp) {
		}
	};

	public Bitmap getAdvertise() {
		return null;
	}

}
