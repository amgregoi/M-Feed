package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

import java.util.List;

import rx.Observable;

public class WebSource {

    private static String wCurrentSource;

    public static String getwCurrentSource() {
        return wCurrentSource;
    }

    public static void setwCurrentSource(String CurrentSource) {
        wCurrentSource = CurrentSource;
    }

    public static String getSourceKey(){
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.SourceKey;
            case (MangaPark.SourceKey):
                return MangaPark.SourceKey;
            default:
                return MangaJoy.SourceKey;
        }
    }

    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getRecentUpdatesObservable();
            case (MangaPark.SourceKey):
                return MangaPark.getRecentUpdatesObservable();
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
            default:
                return MangaJoy.updateMangaObservable(m);
        }
    }

}

