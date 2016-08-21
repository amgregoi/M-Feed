package com.teioh.m_feed.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Manga implements Parcelable {
    public final static String TAG = "MANGA";

    private Long _id;
    private String title;
    private String image;
    private String link;
    private String description;

    private String author;
    private String artist;
    private String genres;
    private String status;
    private String source;
    private String alternate;
    private int following;
    private int initialized;

    public Manga() {
    }

    public Manga(String aTitle, String aUrl, String aSource){
        title = aTitle;
        link = aUrl;
        source = aSource;
        initialized = 0;
        _id = null;
    }

    public Manga(Manga aIn){
        _id = aIn.get_id();
        title = aIn.getTitle();
        image = aIn.getPicUrl();
        link = aIn.getMangaURL();
        description = aIn.getDescription();
        author = aIn.getAuthor();
        artist = aIn.getArtist();
        genres = aIn.getmGenre();
        status = aIn.getStatus();
        source = aIn.getSource();
        alternate = aIn.getAlternate();
        following = aIn.getFollowingValue();
        initialized = getInitialized();
    }

    protected Manga(Parcel aIn) {
        _id = aIn.readLong();
        title = aIn.readString();
        image = aIn.readString();
        link = aIn.readString();
        description = aIn.readString();

        author = aIn.readString();
        artist = aIn.readString();
        genres = aIn.readString();
        status = aIn.readString();
        source = aIn.readString();
        alternate = aIn.readString();
        following = aIn.readInt();
        initialized = aIn.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel aDest, int aFlags) {
        aDest.writeLong(_id);
        aDest.writeString(title);
        aDest.writeString(image);
        aDest.writeString(link);
        aDest.writeString(description);

        aDest.writeString(author);
        aDest.writeString(artist);
        aDest.writeString(genres);
        aDest.writeString(status);
        aDest.writeString(source);
        aDest.writeString(alternate);
        aDest.writeInt(following);
        aDest.writeInt(initialized);
    }

    public static final Creator<Manga> CREATOR = new Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel aIn) {
            return new Manga(aIn);
        }

        @Override
        public Manga[] newArray(int aSize) {
            return new Manga[aSize];
        }
    };

    public long get_id() {
        return _id;
    }

    public void set_id(Long aId) {
        _id = aId;
    }

    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aTitle) {
        title = aTitle;
    }

    public String getDescription(){ return description;}

    public void setDescription(String aDesc){description = aDesc;}

    public String getPicUrl() {
        return image;
    }

    public void setPicUrl(String aPicUrl) {
        image = aPicUrl;
    }

    public String getMangaURL() {
        return link;
    }

    public void setMangaUrl(String aUrl) {
        link = aUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String aAuthor) {
        author = aAuthor;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String aArtist) {
        artist = aArtist;
    }

    public String getmGenre() {
        return genres;
    }

    public void setmGenre(String aGenres) {
        genres = aGenres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String aStatus) {
        status = aStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String aSource) {
        source = aSource;
    }

    public String getAlternate() { return alternate; }

    public void setAlternate(String aAlternate) { alternate = aAlternate; }

    public boolean getFollowing() {
        if(following > 0) return true;
        return false;
    }

    public int getFollowingValue(){
        return following;
    }

    public int setFollowing(int lVal) {
        following = lVal;
        return following;
    }

    public int getInitialized() {
        return initialized;
    }

    public void setInitialized(int aInitialized) {
        initialized = aInitialized;
    }

    @Override public boolean equals(Object aObject) {
        boolean lCompare = false;
        if (aObject != null && aObject instanceof Manga)
        {
            if(getTitle().equals(((Manga)aObject).getTitle()))
                lCompare = true;
        }
        return lCompare;
    }

    public enum FollowType {
        Reading, Completed, On_Hold
    }
}
