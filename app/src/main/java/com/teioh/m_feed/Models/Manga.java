package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Manga implements Parcelable {
    private String mTitle;
    private String mPicUrl;
    private String mMangaUrl;
    private String mDescription;
    private boolean mFollowing;
    private String mAuthor;
    private String mArtist;
    private String mGenre;
    private String mStatus;
    private String mSource;

    public Manga() {
    }

    protected Manga(Parcel in) {
        mTitle = in.readString();
        mPicUrl = in.readString();
        mMangaUrl = in.readString();
        mDescription = in.readString();

        mAuthor = in.readString();
        mArtist = in.readString();
        mGenre = in.readString();
        mStatus = in.readString();
        mSource = in.readString();
        mFollowing = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPicUrl);
        dest.writeString(mMangaUrl);
        dest.writeString(mDescription);
        dest.writeString(mAuthor);
        dest.writeString(mArtist);
        dest.writeString(mGenre);
        dest.writeString(mStatus);
        dest.writeString(mSource);
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

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription(){ return this.mDescription;}

    public void setDescription(String desc){this.mDescription = desc;}

    public String getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(String picUrl) {
        this.mPicUrl = picUrl;
    }

    public String getMangaURL() {
        return mMangaUrl;
    }

    public void setMangaUrl(String url) {
        this.mMangaUrl = url;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmSource() {
        return mSource;
    }

    public void setmSource(String mSource) {
        this.mSource = mSource;
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


    @Override public boolean equals(Object object) {
        boolean sameSame = false;
        if (object != null && object instanceof Manga)
        {
            if(this.getTitle().equals(((Manga)object).getTitle()))
                sameSame = true;
        }
        return sameSame;
    }
}
