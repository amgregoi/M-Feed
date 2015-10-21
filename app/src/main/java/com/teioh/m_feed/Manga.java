package com.teioh.m_feed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Manga implements Parcelable {
    private String mTitle;
    private String mLatestChapter;
    private String mPicUrl;
    private Date mLastUpdated;
    private String mMangaUrl;
    private String mMangaId;

    protected Manga(Parcel in) {
        mTitle = in.readString();
        mLatestChapter = in.readString();
        mPicUrl = in.readString();
        mMangaUrl = in.readString();
        mMangaId = in.readString();
    }

    public Manga() {
    }

    public static final Creator<Manga> CREATOR = new Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel in) {
            return new Manga(in);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getLatestChapter() {
        return mLatestChapter;
    }

    public String getPicUrl() {
        return mPicUrl;
    }

    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public String getMangaURL() {
        return mMangaUrl;
    }

    public String getMangaId() {
        return mMangaId;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setLatestChapter(String latestChapter) {
        this.mLatestChapter = latestChapter;
    }

    public void setPicUrl(String picUrl) {
        this.mPicUrl = picUrl;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.mLastUpdated = lastUpdate;
    }

    public void setMangaUrl(String url) {
        this.mMangaUrl = url;
    }

    public void setMangaId(String id) {
        this.mMangaId = id;
    }

    public String toString() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mLatestChapter);
        dest.writeString(mPicUrl);
        dest.writeString(mMangaUrl);
        dest.writeString(mMangaId);
    }
}
