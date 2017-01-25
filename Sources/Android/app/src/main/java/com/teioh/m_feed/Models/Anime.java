package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Asus1 on 11/6/2015.
 */
public class Anime implements Parcelable
{

    private String mAnimeUrl;
    private String mPictureUrl;
    private String mTitle;
    private String mAlternateNames;
    private String mGenres;
    private String mStatus;
    private String mSummary;
    private boolean mFollowing;
    private String mSource;


    public boolean ismFollowing()
    {
        return mFollowing;
    }

    public void setmFollowing(boolean aFollowing)
    {
        mFollowing = aFollowing;
    }

    public String getmSource()
    {
        return mSource;
    }

    public void setmSource(String aSource)
    {
        aSource = aSource;
    }

    public String getmPictureUrl()
    {
        return mPictureUrl;
    }

    public void setmPictureUrl(String aPictureUrl)
    {
        mPictureUrl = aPictureUrl;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String aTitle)
    {
        mTitle = aTitle;
    }

    public String getmAlternateNames()
    {
        return mAlternateNames;
    }

    public void setmAlternateNames(String aAlternateNames)
    {
        mAlternateNames = aAlternateNames;
    }

    public String getmGenres()
    {
        return mGenres;
    }

    public void setmGenres(String aGenres)
    {
        mGenres = aGenres;
    }

    public String getmStatus()
    {
        return mStatus;
    }

    public void setmStatus(String aStatus)
    {
        mStatus = aStatus;
    }

    public String getmSummary()
    {
        return mSummary;
    }

    public void setmSummary(String aSummary)
    {
        mSummary = aSummary;
    }

    public String getmAnimeUrl()
    {
        return mAnimeUrl;
    }

    public void setmAnimeUrl(String aAnimeUrl)
    {
        mAnimeUrl = aAnimeUrl;
    }

    public Anime()
    {
    }

    public Anime(String aUrl, String aTitle)
    {
        mAnimeUrl = aUrl;
        mTitle = aTitle;
    }


    protected Anime(Parcel aIn)
    {
        mAnimeUrl = aIn.readString();
        mPictureUrl = aIn.readString();
        mTitle = aIn.readString();
        mAlternateNames = aIn.readString();
        mGenres = aIn.readString();
        mStatus = aIn.readString();
        mSummary = aIn.readString();
        mSource = aIn.readString();
        mFollowing = aIn.readByte() != 0;

    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>()
    {
        @Override
        public Anime createFromParcel(Parcel in)
        {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size)
        {
            return new Anime[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel aDest, int aFlags)
    {
        aDest.writeString(mAnimeUrl);
        aDest.writeString(mPictureUrl);
        aDest.writeString(mTitle);
        aDest.writeString(mAlternateNames);
        aDest.writeString(mGenres);
        aDest.writeString(mStatus);
        aDest.writeString(mSummary);
        aDest.writeString(mSource);
        aDest.writeByte((byte) (mFollowing ? 1 : 0));

    }

}
