package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class WebSource {

    private static String wCurrentSource = MangaJoy.SourceKey;

    private static String[] wSources = {MangaHere.SourceKey, MangaPark.SourceKey, MangaJoy.SourceKey};

    public static String getCurrentSource() {
        return wCurrentSource;
    }

    public static void setwCurrentSource(String CurrentSource) {
        wCurrentSource = CurrentSource;
    }

    public static List<String> getSourceList(){
        return Arrays.asList(wSources);
    }

    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getRecentUpdatesObservable();
            case (MangaPark.SourceKey):
                return MangaPark.getRecentUpdatesObservable();
            case (MangaHere.SourceKey):
                return MangaHere.getRecentUpdatesObservable();
            default:
                return MangaJoy.getRecentUpdatesObservable();
        }
    }

    public static Observable<List<Chapter>> getChapterListObservable(final String url) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getChapterListObservable(url);
            case (MangaPark.SourceKey):
                return MangaPark.getChapterListObservable(url);
            case (MangaHere.SourceKey):
                return MangaHere.getChapterListObservable(url);
            default:
                return MangaJoy.getChapterListObservable(url);
        }
    }

    public static Observable<List<String>> getChapterImageListObservable(final String url) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getChapterImageListObservable(url);
            case (MangaPark.SourceKey):
                return MangaPark.getChapterImageListObservable(url);
            case (MangaHere.SourceKey):
                return MangaHere.getChapterImageListObservable(url);
            default:
                return MangaJoy.getChapterImageListObservable(url);
        }
    }

    public static Observable<Manga> updateMangaObservable(final Manga m) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.updateMangaObservable(m);
            case (MangaPark.SourceKey):
                return MangaPark.updateMangaObservable(m);
            case (MangaHere.SourceKey):
                return MangaHere.updateMangaObservable(m);
            default:
                return MangaJoy.updateMangaObservable(m);
        }
    }

}

