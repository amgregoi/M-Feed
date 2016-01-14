package com.teioh.m_feed.Utils.Database;

import android.util.Log;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ReactiveQueryManager {

    /*
     * LibraryFragment gets whole library
     */
    public static Observable<List<Manga>> getMangaLibraryObservable() {
        return pullMangaFromDatabase()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<Manga>>() {
                    @Override
                    public List<Manga> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
    }

    private static Observable<List<Manga>> pullMangaFromDatabase() {
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
        QueryResultIterable<Manga> itr = cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getReadableDatabase()).query(Manga.class)
                .withSelection("mSource = ?", WebSource.getSourceKey())
                .query();

        for (Manga manga : itr) {
            mangaList.add(manga);
        }
        itr.close();
        Log.i("Library Database", "Finished loading library");
        return mangaList;
    }


    /*
     * FollowedFragment gets manga in users library
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
                .query(Manga.class).withSelection("mFollowing = ? AND mSource = ?", "1", WebSource.getSourceKey()).query();
        for (Manga manga : itr) {
            mangaList.add(manga);
        }
        itr.close();
        return mangaList;
    }


    public static Observable<List<Manga>> updateRecentMangaListObservable(ArrayList<Manga> list) {
        return updateRecentMangaList(list)
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

    private static Observable<List<Manga>> updateRecentMangaList(ArrayList<Manga> list) {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    subscriber.onNext(updateList(list));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Manga> updateList(ArrayList<Manga> list){
        ArrayList<Manga> newMangaList = new ArrayList<>();
        for(Manga manga : list){
            Manga testManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).get(manga);
            if(testManga != null){
                newMangaList.add(testManga);
            }
        }

        return newMangaList;
    }
}
