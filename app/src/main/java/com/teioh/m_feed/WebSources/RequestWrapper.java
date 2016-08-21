package com.teioh.m_feed.WebSources;

import android.os.Parcel;
import android.os.Parcelable;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

public class RequestWrapper implements Parcelable {
    public static final String TAG = RequestWrapper.class.getSimpleName();

    private Manga mManga;
    private Chapter mChapter;

    /***
     * Request Wrapper Constructor
     *
     * @param aManga
     */
    public RequestWrapper(Manga aManga) {
        mManga = aManga;
    }

    /***
     * Request Wrapper Constructor
     *
     * @param aChapter
     */
    public RequestWrapper(Chapter aChapter) {
        mChapter = aChapter;
    }

    public static final Parcelable.Creator<RequestWrapper> CREATOR = new Parcelable.Creator<RequestWrapper>() {
        @Override
        public RequestWrapper createFromParcel(Parcel aInputParcel) {
            return new RequestWrapper(aInputParcel);
        }

        @Override
        public RequestWrapper[] newArray(int aSize) {
            return new RequestWrapper[aSize];
        }
    };

    private RequestWrapper(Parcel aIn) {
        mManga = aIn.readParcelable(ClassLoader.getSystemClassLoader());
    }

    /***
     * TODO...
     *
     * @return
     */
    public String getSource() {
        return mManga.getSource();
    }

    /***
     * TODO...
     *
     * @return
     */
    public String getMangaUrl() {
        return mManga.getMangaURL();
    }

    /***
     * TODO...
     *
     * @return
     */
    public String getMangaTitle() {
        return mManga.getTitle();
    }

    /***
     * TODO...
     *
     * @return
     */
    public String getChapterUrl(){
        return mChapter.getChapterUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel aOut, int aFlags) {
        aOut.writeParcelable(mManga, 0);
    }
}