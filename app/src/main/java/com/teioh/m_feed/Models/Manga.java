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

    public Manga(String title, String url, String source){
        this.title = title;
        this.link = url;
        this.source = source;
        this.initialized = 0;
        this._id = null;
    }

    public Manga(Manga in){
        _id = in.get_id();
        title = in.getTitle();
        image = in.getPicUrl();
        link = in.getMangaURL();
        description = in.getDescription();
        author = in.getAuthor();
        artist = in.getArtist();
        genres = in.getmGenre();
        status = in.getStatus();
        source = in.getSource();
        alternate = in.getAlternate();
        following = in.getFollowingValue();
        initialized = getInitialized();
    }

    protected Manga(Parcel in) {
        _id = in.readLong();
        title = in.readString();
        image = in.readString();
        link = in.readString();
        description = in.readString();

        author = in.readString();
        artist = in.readString();
        genres = in.readString();
        status = in.readString();
        source = in.readString();
        alternate = in.readString();
        following = in.readInt();
        initialized = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(link);
        dest.writeString(description);

        dest.writeString(author);
        dest.writeString(artist);
        dest.writeString(genres);
        dest.writeString(status);
        dest.writeString(source);
        dest.writeString(alternate);
        dest.writeInt(following);
        dest.writeInt(initialized);
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

    public long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription(){ return this.description;}

    public void setDescription(String desc){this.description = desc;}

    public String getPicUrl() {
        return image;
    }

    public void setPicUrl(String picUrl) {
        this.image = picUrl;
    }

    public String getMangaURL() {
        return link;
    }

    public void setMangaUrl(String url) {
        this.link = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getmGenre() {
        return genres;
    }

    public void setmGenre(String mGenre) {
        this.genres = mGenre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAlternate() { return alternate; }

    public void setAlternate(String alt) { this.alternate = alt; }

    public boolean getFollowing() {
        if(this.following > 0) return true;
        return false;
    }

    public int getFollowingValue(){
        return following;
    }

    public int setFollowing(int val) {
        this.following = val;
        return this.following;
    }

    public int getInitialized() {
        return initialized;
    }

    public void setInitialized(int initialized) {
        this.initialized = initialized;
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

    public enum FollowType {
        Reading, Completed, On_Hold
    }
}
