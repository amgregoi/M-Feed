package com.teioh.m_feed.WebSources.Sources;

import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.Source;

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

public class MangaEden extends Source {

    final public String SourceKey = "MangaEden";

    final String mBaseUrl = "http://www.mangaeden.com";
    final String mUpdatesUrl = "http://www.mangaeden.com/ajax/news/1/0/";

    /**
     * builds list of manga for recently updated page
     */
    public Observable<List<Manga>> getRecentUpdatesObservable() {
        return NetworkService.getTemporaryInstance()
                .getResponse(mUpdatesUrl)
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeUpdatestoManga(Jsoup.parse(html))))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(Throwable::printStackTrace);
    }

    /***
     * TODO...
     *
     * @param parsedDocument
     * @return
     */
    private List<Manga> scrapeUpdatestoManga(final Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("body > li");

        for (Element htmlBlock : mangaElements) {
            Element urlElement = htmlBlock.select("div.newsManga").first();
            Element nameElement = htmlBlock.select("div.manga_tooltop_header > a").first();

            String mangaTitle = nameElement.text();
            String mangaUrl = "https://www.mangaeden.com/api/manga/" + urlElement.id().substring(0, 24) + "/";

            Manga lManga = MFDBHelper.getInstance().getManga(mangaUrl, SourceKey);
            if (lManga != null) {
                mangaList.add(lManga);
            } else {
                lManga = new Manga(mangaTitle, mangaUrl, SourceKey);
                mangaList.add(lManga);
                MFDBHelper.getInstance().putManga(lManga);
                updateMangaObservable(new RequestWrapper(lManga))
                        .subscribeOn(Schedulers.computation())
                        .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                        .subscribe();
            }
        }

        Log.i("Pull Recent Updates", "Finished pulling updates");
        if (mangaList.size() == 0) return null;
        return mangaList;
    }


    /***
     * TODO...
     *
     * @param request
     * @return
     */
    public Observable<List<Chapter>> getChapterListObservable(final RequestWrapper request) {
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

    /***
     * TODO...
     *
     * @param request
     * @param unparsedJson
     * @return
     * @throws JSONException
     */
    private List<Chapter> parseJsonToChapters(RequestWrapper request, String unparsedJson) throws JSONException {
        JSONObject parsedJsonObject = new JSONObject(unparsedJson);

        List<Chapter> chapterList = scrapeChaptersFromParsedJson(parsedJsonObject);
        chapterList = setNumberForChapterList(chapterList);

        return chapterList;
    }

    /***
     * TODO...
     *
     * @param parsedJsonObject
     * @return
     * @throws JSONException
     */
    private List<Chapter> scrapeChaptersFromParsedJson(JSONObject parsedJsonObject) throws JSONException {
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

    /***
     * TODO...
     *
     * @param chapterNode
     * @param mangaName
     * @return
     * @throws JSONException
     */
    private Chapter constructChapterFromJSONArray(JSONArray chapterNode, String mangaName) throws JSONException {
        Chapter newChapter = new Chapter(mangaName);

        newChapter.setChapterUrl("https://www.mangaeden.com/api/chapter/" + chapterNode.getString(3) + "/");
        newChapter.setChapterTitle(mangaName + " " + chapterNode.getDouble(0));

        Date d = new Date(chapterNode.getLong(1) * 1000);
        newChapter.setChapterDate(d.toString());
        return newChapter;
    }

    /***
     * TODO...
     *
     * @param chapterList
     * @return
     */
    private List<Chapter> setNumberForChapterList(List<Chapter> chapterList) {
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

    /***
     * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
     *
     * @param request
     * @return
     */
    public Observable<String> getChapterImageListObservable(final RequestWrapper request) {
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

    /***
     * TODO...
     *
     * @param unparsedJson
     * @return
     * @throws JSONException
     */
    private List<String> parseJsonToImageUrls(String unparsedJson) throws JSONException {
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


    /***
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @param request
     * @return
     */
    public Observable<Manga> updateMangaObservable(final RequestWrapper request) {
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
                }))
                .onErrorReturn(null)
                .doOnError(throwable -> throwable.printStackTrace());
    }

    /***
     * TODO...
     *
     * @param request
     * @param unparsedJson
     * @return
     * @throws JSONException
     */
    private Manga parseJsonToManga(RequestWrapper request, String unparsedJson) throws JSONException {
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

        Manga newManga = MFDBHelper.getInstance().getManga(request.getMangaUrl(), SourceKey);

        newManga.setArtist(parsedJsonObject.getString("artist"));
        newManga.setAuthor(parsedJsonObject.getString("author"));
        newManga.setDescription(parsedJsonObject.getString("description").trim());
        newManga.setmGenre(fieldGenre);
        newManga.setPicUrl("https://cdn.mangaeden.com/mangasimg/" + parsedJsonObject.getString("image"));
        newManga.setInitialized(1);


        Log.e("MANGAEDEN", newManga.getTitle() + ": updated");
        MFDBHelper.getInstance().putManga(newManga);
        return newManga;
    }
}