package com.teioh.m_feed;

import android.util.Log;

import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Models.Manga;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;

import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ReactiveQueryManager {

    /*
     * Tab3 gets whole library
     */
    public static Observable<List<Manga>> getMangaLibraryObservable() {
        return pullMangaFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<Manga>>() {
                    @Override
                    public List<Manga> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
    }

    public static Observable<List<Manga>> pullMangaFromDatabase() {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    subscriber.onNext(getLibraryFromDatabase());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Manga> getLibraryFromDatabase() {
        ArrayList<Manga> mangaList = new ArrayList<>();
        QueryResultIterable<Manga> itr = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class).query();
        for (Manga manga : itr) {
            mangaList.add(manga);
        }
        itr.close();
        Log.i("Library Database", "Finished loading library");
        return mangaList;
    }


    /*
     * Tab2 gets manga in users library
     */
    public static Observable<List<Manga>> getFollowedMangaObservable() {
        return pullFollowedMangaFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<Manga>>() {
                    @Override
                    public List<Manga> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
    }

    private static Observable<List<Manga>> pullFollowedMangaFromDatabase() {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    subscriber.onNext(getFollowedManga());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Manga> getFollowedManga() {
        ArrayList<Manga> mangaList = new ArrayList<>();
        QueryResultIterable<Manga> itr = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Manga.class).withSelection("mFollowing = ?", "1").query();
        for (Manga manga : itr) {
            mangaList.add(manga);
        }
        itr.close();
        return mangaList;
    }
}
