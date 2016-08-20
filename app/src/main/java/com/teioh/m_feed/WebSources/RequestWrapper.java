package com.teioh.m_feed.WebSources;

import android.os.Parcel;
import android.os.Parcelable;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

public class RequestWrapper implements Parcelable {
    public static final String TAG = RequestWrapper.class.getSimpleName();

    private Manga mManga;
    private Chapter mChapter;

    public RequestWrapper(Manga manga) {
        mManga = manga;
    }
    public RequestWrapper(Chapter chapter) {
        mChapter = chapter;
    }

    public static final Parcelable.Creator<RequestWrapper> CREATOR = new Parcelable.Creator<RequestWrapper>() {
        @Override
        public RequestWrapper createFromParcel(Parcel inputParcel) {
            return new RequestWrapper(inputParcel);
        }

        @Override
        public RequestWrapper[] newArray(int size) {
            return new RequestWrapper[size];
        }
    };

    private RequestWrapper(Parcel in) {
        mManga = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    public String getSource() {
        return mManga.getSource();
    }

    public String getMangaUrl() {
        return mManga.getMangaURL();
    }

    public String getMangaTitle() {
        return mManga.getTitle();
    }

    public String getChapterUrl(){ return mChapter.getChapterUrl(); }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mManga, 0);
    }
}