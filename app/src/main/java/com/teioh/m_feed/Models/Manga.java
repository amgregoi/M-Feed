package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

//TODO - subclassing parse object
public class Manga implements Parcelable {
    private String mTitle;
    private String mLatestChapter;
    private String mPicUrl;
    private String mMangaUrl;
    private Date mLastUpdated;
    private String mMangaId;
    private String mDescription;
    private boolean mFollowing;
    private int mDBID;

    public Manga() {
    }

    protected Manga(Parcel in) {
        mTitle = in.readString();
        mLatestChapter = in.readString();
        mPicUrl = in.readString();
        mMangaUrl = in.readString();
        mMangaId = in.readString();
        mDescription = in.readString();
        mDBID = in.readInt();
        mFollowing = in.readByte() != 0;
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
        dest.writeString(mDescription);
        dest.writeInt(mDBID);
        dest.writeByte((byte) (mFollowing ? 1 : 0));
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

    public String getDescription(){ return this.mDescription;}

    public void setDescription(String desc){this.mDescription = desc;}

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

    public void setLastUpdate() {
        this.mLastUpdated = new Date();
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

    public boolean getFollowing() {
        return this.mFollowing;
    }

    public boolean setFollowing(boolean val) {
        this.mFollowing = val;
        return this.mFollowing;
    }

    public int getDBID() {
        return this.mDBID;
    }

    public void setDBID(int val) {
        this.mDBID = val;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Manga)
        {
            if(this.getTitle().equals(((Manga)object).getTitle()))
                sameSame = true;
        }
        return sameSame;
    }
}
