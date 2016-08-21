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

    public Chapter(String aTitle) {
        mangaTitle = aTitle;
        chapterTitle = aTitle;
    }

    public Chapter(String aUrl, String aMangaTitle, String aChapterTitle, String aDate, int aNum) {
        url = aUrl;
        date = aDate;
        mangaTitle = aMangaTitle;
        chapterTitle = aChapterTitle;
        chapterNumber = aNum;
    }

    public Chapter(String aUrl, String aMangaTitle, String aChapterTitle, String aDate) {
        url = aUrl;
        date = aDate;
        mangaTitle = aMangaTitle;
        chapterTitle = aChapterTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel aDest, int aFlags) {
        aDest.writeString(url);
        aDest.writeString(date);
        aDest.writeString(mangaTitle);
        aDest.writeString(chapterTitle);
        aDest.writeInt(chapterNumber);
        aDest.writeInt(currentPage);
        aDest.writeInt(totalPages);
    }

    protected Chapter(Parcel aIn) {
        url = aIn.readString();
        date = aIn.readString();
        mangaTitle = aIn.readString();
        chapterTitle = aIn.readString();
        chapterNumber = aIn.readInt();
        currentPage = aIn.readInt();
        totalPages = aIn.readInt();
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
        return url;
    }

    public void setChapterUrl(String aUrl) {
        url = aUrl;
    }

    public String getChapterDate() {
        return date;
    }

    public void setChapterDate(String aDate) {
        aDate = aDate;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String aTitle) {
        chapterTitle = aTitle;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int aNum) {
        chapterNumber = aNum;
    }

    public String toString() {
        return chapterTitle;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public void setMangaTitle(String aTitle) {
        mangaTitle = aTitle;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int aCurrentPage) {
        currentPage = aCurrentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int aTotlePages) {
        totalPages = aTotlePages;
    }


}
