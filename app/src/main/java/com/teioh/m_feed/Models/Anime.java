package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Asus1 on 11/6/2015.
 */
public class Anime implements Parcelable {

    private String mAnimeUrl;
    private String mPictureUrl;
    private String mTitle;
    private String mAlternateNames;
    private String mGenres;
    private String mStatus;
    private String mSummary;


    public boolean ismFollowing() {
        return mFollowing;
    }

    public void setmFollowing(boolean mFollowing) {
        this.mFollowing = mFollowing;
    }

    private boolean mFollowing;

    public String getmSource() {
        return mSource;
    }

    public void setmSource(String mSource) {
        this.mSource = mSource;
    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public void setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmAlternateNames() {
        return mAlternateNames;
    }

    public void setmAlternateNames(String mAlternateNames) {
        this.mAlternateNames = mAlternateNames;
    }

    public String getmGenres() {
        return mGenres;
    }

    public void setmGenres(String mGenres) {
        this.mGenres = mGenres;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmSummary() {
        return mSummary;
    }

    public void setmSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getmAnimeUrl() {
        return mAnimeUrl;
    }

    public void setmAnimeUrl(String mAnimeUrl) {
        this.mAnimeUrl = mAnimeUrl;
    }

    private String mSource;

    public Anime(){}
    public Anime(String animeUrl, String title){
        this.mAnimeUrl = animeUrl;
        this.mTitle = title;
    }



    protected Anime(Parcel in) {
        mAnimeUrl = in.readString();
        mPictureUrl = in.readString();
        mTitle = in.readString();
        mAlternateNames = in.readString();
        mGenres = in.readString();
        mStatus = in.readString();
        mSummary = in.readString();
        mSource = in.readString();
        mFollowing = in.readByte() != 0;

    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAnimeUrl);
        dest.writeString(mPictureUrl);
        dest.writeString(mTitle);
        dest.writeString(mAlternateNames);
        dest.writeString(mGenres);
        dest.writeString(mStatus);
        dest.writeString(mSummary);
        dest.writeString(mSource);
        dest.writeByte((byte) (mFollowing ? 1 : 0));

    }

}
