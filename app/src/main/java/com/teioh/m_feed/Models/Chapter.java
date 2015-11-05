package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Asus1 on 11/1/2015.
 */
public class Chapter implements Parcelable{
    String mUrl;
    String mDate;
    String mTitle;
    String cTitle;
    int cNumber;

    public Chapter() {
    }

    public Chapter(String url, String mTitle, String cTitle, String date, int num) {
        this.mUrl = url;
        this.mDate = date;
        this.mTitle = mTitle;
        this.cTitle = cTitle;
        this.cNumber = num;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cNumber);
        dest.writeString(mUrl);
        dest.writeString(mDate);
        dest.writeString(mTitle);
    }

    protected Chapter(Parcel in) {
        mUrl = in.readString();
        mDate = in.readString();
        mTitle = in.readString();
        cNumber = in.readInt();
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public String getChapterUrl() {
        return this.mUrl;
    }

    public void setChapterUrl(String url) {
        this.mUrl = url;
    }

    public String getChapterDate() {
        return this.mDate;
    }

    public void setChapterDate(String date) {
        this.mDate = date;
    }

    public String getChapterTitle() {
        return this.cTitle;
    }

    public void setChapterTitle(String title) {
        this.cTitle = title;
    }

    public int getChapterNumber(){return this.cNumber;}

    public void setChapterNumber(int num){this.cNumber = num;}

    public String toString()
    {
        return this.mTitle;
    }

    public String getMangaTitle() {
        return this.mTitle;
    }
}
