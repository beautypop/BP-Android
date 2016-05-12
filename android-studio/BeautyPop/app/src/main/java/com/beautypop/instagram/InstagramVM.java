package com.beautypop.instagram;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JAY GANESH on 5/6/2016.
 */
public class InstagramVM implements Parcelable {

	public String url;
	public String caption;
	public String mediaId;
	public  boolean isImported;

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
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(this.url);
		dest.writeString(this.caption);
		dest.writeString(this.mediaId);
	}

	public InstagramVM(Parcel in) {
		this.url = in.readString();
		this.caption = in.readString();
		this.mediaId = in.readString();
	}

	public InstagramVM() {

	}


	public static final Creator CREATOR = new Creator() {
		public InstagramVM createFromParcel(Parcel in) {
			return new InstagramVM(in);
		}

		public InstagramVM[] newArray(int size) {
			return new InstagramVM[size];
		}
	};

	public boolean isImported() {
		return isImported;
	}

	public void setIsImported(boolean isImported) {
		this.isImported = isImported;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
