package com.teioh.m_feed.WebSources;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaHere {

    final public static String SourceKey = "MangaHere";

    final static String MangaHereUrl = "http://mangahere.co/";
    final static String MangaHereUpdates = "http://mangahere.co/latest/";

    /**
     * builds list of manga for recently updated page
     * @return
     */
    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        return pullUpdatedMangaFromWebsite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .onErrorReturn(throwable -> {
                    Log.e("throwable", throwable.toString());
                    return null;
                }).doOnError(Throwable::printStackTrace);
    }

    private static Observable<List<Manga>> pullUpdatedMangaFromWebsite() {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(MangaHereUpdates)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .ignoreHttpErrors(true)
                            .timeout(10000);
                    String unparsedHtml = null;
                    int code = connect.execute().statusCode();
                    if (code == 200) {
                        unparsedHtml = connect.get().html();
                    }

                    subscriber.onNext(parseRecentUpdatesToManga(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Manga> parseRecentUpdatesToManga(final String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.manga_updates");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    private static List<Manga> scrapeUpdatestoManga(final Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("dl");

        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("dt");
            for (Element usefulElement : usefulElements) {
                String mangaTitle = usefulElement.select("a").attr("rel");
                String mangaUrl = usefulElement.select("a").attr("href");
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("mTitle = ? AND mSource = ?", mangaTitle, SourceKey).get();
                if (manga != null) {
                    mangaList.add(manga);
                } else {
                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    mangaList.add(manga);
                    cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
                    Observable<Manga> observableManga = MangaJoy.updateMangaObservable(manga);
                    observableManga.subscribe();
                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        if(mangaList.size() == 0) return null;
        return mangaList;
    }

    /**
     * builds list of chapters for manga object
     * @return
     */

    public static Observable<List<Chapter>> getChapterListObservable(final String url) {
        return pullChaptersFromWebsite(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .onErrorReturn(new Func1<Throwable, List<Chapter>>() {
                    @Override
                    public List<Chapter> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                }).doOnError(throwable -> throwable.printStackTrace());
    }

    private static Observable<List<Chapter>> pullChaptersFromWebsite(final String url) {
        return Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
            @Override
            public void call(Subscriber<? super List<Chapter>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(url.toLowerCase())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200)
                        unparsedHtml = connect.get().html().toString();

                    subscriber.onNext(parseHtmlToChapters(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Chapter> parseHtmlToChapters(final String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.detail_list").select("ul").not("ul.tab_comment.clearfix");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument);
        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();

        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
            String title = chapterElement.select("a").text();
            String cTitle = chapterElement.select("span.left").text();
            String chapterDate = chapterElement.select("span.right").text();

            Chapter curChapter = new Chapter(chapterUrl, title, cTitle, chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
        }
        return chapterList;
    }


    /**
    * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
     * @return
    */
    public static Observable<List<String>> getChapterImageListObservable(final String url) {
        return parseListOfImageUrls(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<String>>() {
                    @Override
                    public List<String> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                })
                .doOnError(throwable -> throwable.printStackTrace());
    }

    private static Observable<List<String>> parseListOfImageUrls(final String url) {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);
                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200) {
                        unparsedHtml = connect.get().html();
                    }

                    subscriber.onNext(getUrlList(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<String> getUrlList(final String unparsedHtml) {
        List<String> pageUrls = new ArrayList<>();
        List<String> imageUnparsedHtml = new ArrayList<>();

        //get base url for images
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdates = parsedDocumentForImage.select("select.wid60").first().select("option");
        for(Element url: imageUpdates){
            pageUrls.add(url.attr("value"));
        }

        for(String url : pageUrls) {
            try {
                Connection connect = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000);
                if (connect.execute().statusCode() == 200) {
                    imageUnparsedHtml.add(connect.get().html());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buildImageUrlList(imageUnparsedHtml);
    }

    private static List<String> buildImageUrlList(final List<String> unparsedHtml) {
        List<String> imageUrls = new ArrayList<>();
        for(String s : unparsedHtml){
            Document parsedDocumentForImage = Jsoup.parse(s);
            Elements imageUpdate = parsedDocumentForImage.select("section#viewer.read_img");
            parsedDocumentForImage = Jsoup.parse(imageUpdate.toString());
            imageUrls.add(parsedDocumentForImage.select("img#image").attr("src"));
        }
        return imageUrls;
    }

    /**
    * Adds new Manga and
    * gets missing manga information and updates database
     * @return
    */
    public static Observable<Manga> updateMangaObservable(final Manga m) {
        return getUnparsedHtml(m)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .onErrorReturn(new Func1<Throwable, Manga>() {
                    @Override
                    public Manga call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                })
                .doOnError(throwable -> throwable.printStackTrace());
    }

    private static Observable<Manga> getUnparsedHtml(final Manga m) {
        return Observable.create(new Observable.OnSubscribe<Manga>() {
            @Override
            public void call(Subscriber<? super Manga> subscriber) {
                try {
                    Connection connect = Jsoup.connect(m.getMangaURL().toLowerCase())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200)
                        unparsedHtml = connect.get().html();

                    subscriber.onNext(scrapeAndUpdateManga(unparsedHtml, m.getMangaURL()));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Manga scrapeAndUpdateManga(final String unparsedHtml, String url) {
        Document html = Jsoup.parse(unparsedHtml);
        Elements usefulSection = html.select("div.manga_detail_top.clearfix");

        //image url
        Element imageElement = usefulSection.select("img").first();
        //summary
        Elements e = usefulSection.select("ul.detail_topText").select("li");


        String img = imageElement.attr("src");
        String summary = null;
        String alternate = null;
        String author = null;
        String artist = null;
        String genres = null;
        String status = null;
        for (int i = 0; i < e.size(); i++) {
            if (i == 2) {
                alternate = e.get(i).text().replace(e.get(i).select("label").text(), "");
            } else if (i == 4) {
                author = e.get(i).text().replace(e.get(i).select("label").text(), "");
            } else if (i == 5) {
                artist = e.get(i).text().replace(e.get(i).select("label").text(), "");
            } else if (i == 3) {
                genres = e.get(i).text().replace(e.get(i).select("label").text(), "");
            } else if( i == 6) {
                status = e.get(i).text().replace(e.get(i).select("label").text(), "");
            }else if (i == 8) {
                summary = e.get(i).text();
            }
        }


        ContentValues values = new ContentValues(1);
        values.put("mAlternate", alternate);
        values.put("mPicUrl", img);
        values.put("mDescription", summary);
        values.put("mArtist", artist);
        values.put("mAuthor", author);
        values.put("mGenres", genres);
        values.put("mStatus", status);
        values.put("mSource", SourceKey);

        cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getWritableDatabase())
                .update(Manga.class, values, "mMangaUrl = ?", url);

        return cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getReadableDatabase()).query(Manga.class)
                .withSelection("mMangaUrl = ? AND mSource = ?", url, SourceKey).get();
    }
}