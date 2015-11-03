package com.teioh.m_feed.Pojo;

/**
 * Created by Asus1 on 11/1/2015.
 */
public class Chapter {
    String mUrl;
    String mDate;
    String mTitle;
    int cNumber;

    public Chapter() {
    }

    public Chapter(String url, String title, String date, int num) {
        this.mUrl = url;
        this.mDate = date;
        this.mTitle = title;
        this.cNumber = num;
    }


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
        return this.mTitle;
    }

    public void setChapterTitle(String title) {
        this.mTitle = title;
    }

    public int getChapterNumber(){return this.cNumber;}

    public void setChapterNumber(int num){this.cNumber = num;}

    public String toString()
    {
        return this.mTitle;
    }

}
