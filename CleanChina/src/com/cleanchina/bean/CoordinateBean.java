package com.cleanchina.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CoordinateBean implements Parcelable {

	public int tlx;
	public int tly;

	public int brx;
	public int bry;

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof CoordinateBean))
			return false;

		final CoordinateBean c = (CoordinateBean) other;

		return (c.tlx == this.tlx) && (c.tly == this.tly)
				&& (c.brx == this.brx) && (c.bry == this.bry);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public CoordinateBean(Parcel in) {
		tlx = in.readInt();
		tly = in.readInt();
		brx = in.readInt();
		bry = in.readInt();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public CoordinateBean createFromParcel(Parcel in) {
			return new CoordinateBean(in);
		}

		public CoordinateBean[] newArray(int size) {
			return new CoordinateBean[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(tlx);
		dest.writeInt(tly);
		dest.writeInt(brx);
		dest.writeInt(bry);
	}
}
