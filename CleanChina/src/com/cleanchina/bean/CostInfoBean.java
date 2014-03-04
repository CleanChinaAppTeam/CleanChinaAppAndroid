package com.cleanchina.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CostInfoBean implements Parcelable {

	public String zhanweiimg_id;
	public CoordinateBean coordinate;
	public String howbuy;

	@Override
	public int describeContents() {
		return 0;
	}

	public CostInfoBean(Parcel in) {
		zhanweiimg_id = in.readString();
		coordinate = in.readParcelable(CoordinateBean.class.getClassLoader());
		howbuy = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public CostInfoBean createFromParcel(Parcel in) {
			return new CostInfoBean(in);
		}

		public CostInfoBean[] newArray(int size) {
			return new CostInfoBean[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(zhanweiimg_id);
		dest.writeParcelable(coordinate, 0);
		dest.writeString(howbuy);
	}

}
