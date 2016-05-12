package com.beautypop.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JAY GANESH on 5/6/2016.
 */
public class InstagramVM implements Parcelable {

	public String url;
	public String caption;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

	}
}
