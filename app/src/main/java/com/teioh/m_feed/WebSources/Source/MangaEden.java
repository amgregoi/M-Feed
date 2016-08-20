package com.teioh.m_feed.WebSources.Source;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaEden {

    final public static String SourceKey = "MangaEden";

    final static String mBaseUrl = "http://www.mangaeden.com";
    final static String mUpdatesUrl = "http://www.mangaeden.com/ajax/news/1/0/";

    /**
     * builds list of manga for recently updated page
     *
     * @return
     */
    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        return NetworkService.getTemporaryInstance()
                .getResponse(mUpdatesUrl)
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeUpdatestoManga(Jsoup.parse(html))))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(Throwable::printStackTrace);
    }

    private static List<Manga> scrapeUpdatestoManga(final Document parsedDocument) {
        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("body > li");

        for (Element htmlBlock : mangaElements) {
            Element urlElement = htmlBlock.select("div.newsManga").first();
            Element nameElement = htmlBlock.select("div.manga_tooltop_header > a").first();

            String mangaTitle = nameElement.text();
            String mangaUrl = "https://www.mangaeden.com/api/manga/" + urlElement.id().substring(0, 24) + "/";

            Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("title = ? AND source = ?", mangaTitle, SourceKey).get();
            if (manga != null) {
                mangaList.add(manga);
            } else {
                manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                mangaList.add(manga);
                cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
                Observable<Manga> observableManga = MangaEden.updateMangaObservable(new RequestWrapper(manga));
                observableManga.subscribe();
            }
        }

        Log.i("Pull Recent Updates", "Finished pulling updates");
        if (mangaList.size() == 0) return null;
        return mangaList;
    }


    public static Observable<List<Chapter>> getChapterListObservable(final RequestWrapper request) {
        return NetworkService.getPermanentInstance()
                .getResponse(request.getMangaUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedJson -> Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
                    @Override
                    public void call(Subscriber<? super List<Chapter>> subscriber) {
                        try {
                            subscriber.onNext(parseJsonToChapters(request, unparsedJson));
                            subscriber.onCompleted();
                        } catch (Throwable e) {
                            subscriber.onError(e);
                        }
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static List<Chapter> parseJsonToChapters(RequestWrapper request, String unparsedJson) throws JSONException {
        JSONObject parsedJsonObject = new JSONObject(unparsedJson);

        List<Chapter> chapterList = scrapeChaptersFromParsedJson(parsedJsonObject);
        chapterList = setNumberForChapterList(chapterList);

        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedJson(JSONObject parsedJsonObject) throws JSONException {
        List<Chapter> chapterList = new ArrayList<>();

        String mangaName = parsedJsonObject.getString("title");
        JSONArray chapterArrayNodes = parsedJsonObject.getJSONArray("chapters");
        for (int index = 0; index < chapterArrayNodes.length(); index++) {
            JSONArray currentChapterArray = chapterArrayNodes.getJSONArray(index);

            Chapter currentChapter = constructChapterFromJSONArray(currentChapterArray, mangaName);

            chapterList.add(currentChapter);
        }

        Collections.reverse(chapterList);
        return chapterList;
    }

    private static Chapter constructChapterFromJSONArray(JSONArray chapterNode, String mangaName) throws JSONException {
        Chapter newChapter = new Chapter(mangaName);

        newChapter.setChapterUrl("https://www.mangaeden.com/api/chapter/" + chapterNode.getString(3) + "/");
        newChapter.setChapterTitle(mangaName + " " + chapterNode.getDouble(0));

        Date d = new Date(chapterNode.getLong(1) * 1000);
        newChapter.setChapterDate(d.toString());
        return newChapter;
    }

    private static List<Chapter> setNumberForChapterList(List<Chapter> chapterList) {
        Collections.reverse(chapterList);
        for (int index = 0; index < chapterList.size(); index++) {
            chapterList.get(index).setChapterNumber(index + 1);
        }

        return chapterList;
    }

    /*
     *
     *
     *
     *
     *
     *
     *
     *
     */

    /**
     * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
     *
     * @return
     */
    public static Observable<String> getChapterImageList(final RequestWrapper request) {
        return NetworkService.getPermanentInstance()
                .getResponse(request.getChapterUrl())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedJson -> Observable.create(new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {
                        try {
                            subscriber.onNext(parseJsonToImageUrls(unparsedJson));
                            subscriber.onCompleted();
                        } catch (Throwable e) {
                            subscriber.onError(e);
                        }
                    }
                }))
                .flatMap(imageUrls -> Observable.from(imageUrls.toArray(new String[imageUrls.size()])));
    }

    private static List<String> parseJsonToImageUrls(String unparsedJson) throws JSONException {
        JSONObject parsedJson = new JSONObject(unparsedJson);
        List<String> imageUrlList = new ArrayList<>();

        JSONArray imageArrayNodes = parsedJson.getJSONArray("images");
        for (int index = 0; index < imageArrayNodes.length(); index++) {
            JSONArray currentImageNode = imageArrayNodes.getJSONArray(index);

            imageUrlList.add("https://cdn.mangaeden.com/mangasimg/" + currentImageNode.getString(1));
        }
        Collections.reverse(imageUrlList);

        return imageUrlList;
    }

    /**
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @return
     */
    public static Observable<Manga> updateMangaObservable(final RequestWrapper request) {
        NetworkService currService = NetworkService.getTemporaryInstance();
        return currService
                .getResponse(request.getMangaUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedJson -> Observable.create(new Observable.OnSubscribe<Manga>() {
                    @Override
                    public void call(Subscriber<? super Manga> subscriber) {
                        try {
                            subscriber.onNext(parseJsonToManga(request, unparsedJson));
                            subscriber.onCompleted();
                        } catch (Throwable e) {
                            subscriber.onError(e);
                        }
                    }
                }));
    }

    private static Manga parseJsonToManga(RequestWrapper request, String unparsedJson) throws JSONException {
        JSONObject parsedJsonObject = new JSONObject(unparsedJson);

        String fieldGenre = "";
        JSONArray genreArrayNodes = parsedJsonObject.getJSONArray("categories");
        for (int index = 0; index < genreArrayNodes.length(); index++) {
            if (index != genreArrayNodes.length() - 1) {
                fieldGenre += genreArrayNodes.getString(index) + ", ";
            } else {
                fieldGenre += genreArrayNodes.getString(index);
            }
        }

        SQLiteDatabase sqLiteDatabase = MangaFeedDbHelper.getInstance().getWritableDatabase();
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append("source = ?");
        selectionArgs.add(SourceKey);
        selection.append(" AND ").append("link = ?");
        selectionArgs.add(request.getMangaUrl());

        Manga newManga = cupboard().withDatabase(sqLiteDatabase).query(Manga.class)
                .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                .limit(1)
                .get();

        newManga.setArtist(parsedJsonObject.getString("artist"));
        newManga.setAuthor(parsedJsonObject.getString("author"));
        newManga.setDescription(parsedJsonObject.getString("description").trim());
        newManga.setmGenre(fieldGenre);
        newManga.setPicUrl("https://cdn.mangaeden.com/mangasimg/" + parsedJsonObject.getString("image"));
        newManga.setInitialized(1);


        Log.e("MANGAEDEN", newManga.getTitle() + ": updated");
        cupboard().withDatabase(sqLiteDatabase).put(newManga);

        return newManga;
    }
}