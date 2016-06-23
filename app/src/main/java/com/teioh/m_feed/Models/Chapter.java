package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
    public final static String TAG = "CHAPTER";

    private String url;
    private String date;
    private String mangaTitle;
    private String chapterTitle;
    private int chapterNumber;

    private int currentPage;
    private int totalPages;

    public Chapter() {
    }

    public Chapter(String title) {
        this.mangaTitle = title;
        this.chapterTitle = title;
    }

    public Chapter(String url, String mTitle, String cTitle, String date, int num) {
        this.url = url;
        this.date = date;
        this.mangaTitle = mTitle;
        this.chapterTitle = cTitle;
        this.chapterNumber = num;
    }

    public Chapter(String url, String mTitle, String cTitle, String date) {
        this.url = url;
        this.date = date;
        this.mangaTitle = mTitle;
        this.chapterTitle = cTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(date);
        dest.writeString(mangaTitle);
        dest.writeString(chapterTitle);
        dest.writeInt(chapterNumber);
        dest.writeInt(currentPage);
        dest.writeInt(totalPages);
    }

    protected Chapter(Parcel in) {
        url = in.readString();
        date = in.readString();
        mangaTitle = in.readString();
        chapterTitle = in.readString();
        chapterNumber = in.readInt();
        currentPage = in.readInt();
        totalPages = in.readInt();
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
        return this.url;
    }

    public void setChapterUrl(String url) {
        this.url = url;
    }

    public String getChapterDate() {
        return this.date;
    }

    public void setChapterDate(String date) {
        this.date = date;
    }

    public String getChapterTitle() {
        return this.chapterTitle;
    }

    public void setChapterTitle(String title) {
        this.chapterTitle = title;
    }

    public int getChapterNumber() {
        return this.chapterNumber;
    }

    public void setChapterNumber(int num) {
        this.chapterNumber = num;
    }

    public String toString() {
        return this.chapterTitle;
    }

    public String getMangaTitle() {
        return this.mangaTitle;
    }

    public void setMangaTitle(String title) { this.mangaTitle = title; }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int mCurrentPage) {
        this.currentPage = mCurrentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int mTotalPages) {
        this.totalPages = mTotalPages;
    }


}
