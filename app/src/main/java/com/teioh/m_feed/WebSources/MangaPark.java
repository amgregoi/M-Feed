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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaPark {

    final public static String SourceKey = "MangaPark";

    final static String MangaParkUrl = "http://mangapark.me/latest/";
    final static String MangaParkBaseUrl = "http://mangapark.me";

    /*
     * builds list of manga for recently updated page
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
                    Connection connect = Jsoup.connect(MangaParkUrl)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .ignoreHttpErrors(true)
                            .timeout(10000);

                    String unparsedHtml = null;
                    int code = connect.execute().statusCode();
                    if (code == 200) {
                        unparsedHtml = connect.get().html();
                    } else {
                        Log.e("RAWR", Integer.toString(code));
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
        Elements updates = parsedDocument.select("div.item");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    private static List<Manga> scrapeUpdatestoManga(final Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        //Log.e("RAWR", parsedDocument.toString());
        Elements mangaElements = parsedDocument.select("div.item");

        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("ul").select("h3");
            for (Element usefulElement : usefulElements) {
                String mangaTitle = usefulElement.select("a").text();
                String mangaUrl = MangaParkBaseUrl + usefulElement.select("a").attr("href");
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("mTitle = ? AND mSource = ?", mangaTitle, SourceKey).get();
                if (manga != null) {
                    mangaList.add(manga);
                } else {
                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
                    mangaList.add(manga);
                }
//                else {
//                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
//                    cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
//                    Observable<Manga> observableManga = MangaPark.updateMangaObservable(manga);
//                    observableManga.subscribe();
//                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        return mangaList;
    }


    /*
     * builds list of chapters for manga object
     */
    public static Observable<List<Chapter>> getChapterListObservable(final String url) {
        return pullChaptersFromWebsite(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .timeout(25, TimeUnit.SECONDS, Schedulers.io())
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
                        unparsedHtml = connect.get().html();

                    subscriber.onNext(parseHtmlToChapters(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Chapter> parseHtmlToChapters(final String unparsedHtml) {
        int chosenIndex = 0, count = -1, i = 0;
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div#list.book-list").select("div.stream");
        for (Element e : updates) {
            Elements chapters = e.select("ul.chapter").select("li");
            if (chapters.size() > count) {
                count = chapters.size();
                chosenIndex = i;
            }
            i++;
        }
        parsedDocument = Jsoup.parse(updates.get(chosenIndex).toString());
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument);
        return chapterList;


    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.select("ul.chapter").select("li");
        Elements temp;

        for (Element chapterElement : chapterElements) {
            temp = chapterElement.select("span");
            String chapterUrl = MangaParkBaseUrl + temp.select("a").attr("href");
            String[] titles = temp.text().split(" : ");
            String chapterDate = chapterElement.select("i").text();
            Chapter curChapter;
            if (titles.length == 2) {
                curChapter = new Chapter(chapterUrl, titles[0], titles[1], chapterDate);
            } else {
                curChapter = new Chapter(chapterUrl, titles[0], titles[0], chapterDate);
            }
            chapterList.add(curChapter);
        }


        //set chapter numbers
        int numChapters = chapterList.size() - 1;
        for (int i = 0; i <= numChapters; i++) {
            chapterList.get(i).setChapterNumber(numChapters);
            numChapters--;
        }
        return chapterList;
    }


    /*
    * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
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

                    subscriber.onNext(getBaseUrlDirectory(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<String> getBaseUrlDirectory(final String unparsedHtml) {
        //get base url for images
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        String imageUrl = parsedDocumentForImage.select("div.canvas").select("a.img-link").select("img").attr("src");

        //get img extension
        Pattern regex2 = Pattern.compile("(?!.*\\/).*");
        Matcher regexMatcher2 = regex2.matcher(imageUrl);
        regexMatcher2.find();
        String extension = regexMatcher2.group();

        String baseUrl = imageUrl.replace(extension, "");
        String directoryHtml = null;
        try {
            Connection connect = Jsoup.connect(baseUrl)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(10000);
            if (connect.execute().statusCode() == 200) {
                directoryHtml = connect.get().html();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildImageUrlList(directoryHtml, baseUrl);
    }

    private static List<String> buildImageUrlList(final String unparsedHtml, String imageBaseUrl) {
        List<String> imageUrls = new ArrayList<>();
        String postfix;
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdate = parsedDocumentForImage.select("a");

        int i = 0;
        for (Element e : imageUpdate) {
            if (i > 0) {
                postfix = e.select("a").attr("href");
                imageUrls.add(postfix);
            }
            i++;
        }

        //TODO FIX
        Collections.sort(imageUrls, (s1, s2) -> {
            String ss1 = s1.replaceAll("[^0-9]", "");
            String ss2 = s2.replaceAll("[^0-9]", "");
            return Integer.valueOf(ss1).compareTo(Integer.valueOf(ss2));
        });

        for (i = 0; i < imageUrls.size(); i++) {
            imageUrls.set(i, imageBaseUrl + imageUrls.get(i));
        }

        return imageUrls;
    }


    /*
    * Adds new Manga and
    * gets missing manga information and updates database
    */
    public static Observable<Manga> updateMangaObservable(final Manga manga) {
        return getUnparsedHtml(manga)
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

    private static Observable<Manga> getUnparsedHtml(final Manga manga) {
        return Observable.create(new Observable.OnSubscribe<Manga>() {
            @Override
            public void call(Subscriber<? super Manga> subscriber) {
                try {
                    Connection connect = Jsoup.connect(manga.getMangaURL().toLowerCase())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200)
                        unparsedHtml = connect.get().html().toString();

                    subscriber.onNext(scrapeAndUpdateManga(unparsedHtml, manga.getMangaURL()));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Manga scrapeAndUpdateManga(final String unparsedHtml, String url) {
        Document html = Jsoup.parse(unparsedHtml);

        //summary
        String summary = html.body().select("p.summary").text();

        Elements e = html.body().select("table.outer");
        String img = e.select("div.cover").select("img").attr("href");
        if (img.equals("")) {
            img = e.select("div.cover").select("img").attr("src");
        }
        e = e.select("table.attr").select("tbody").select("tr");
        String alternate = null;
        String author = null;
        String artist = null;
        String genres = null;
        String status = null;
        Elements tList;
        for (int i = 0; i < e.size(); i++) {
            if (i == 3) {
                //alternative
                alternate = e.get(i).select("td").text();
            } else if (i == 4) {
                //author
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0) {
                    alternate = "";
                    for (Element auth : tList) {
                        alternate += auth.text() + ",";
                    }
                } else {
                    alternate = "~";
                }
            } else if (i == 5) {
                //artist
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0) {
                    artist = "";
                    for (Element art : tList) {
                        artist += art.text() + ",";
                    }
                } else {
                    artist = "~";
                }
            } else if (i == 6) {
                //genres
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0) {
                    genres = "";
                    for (Element gen : tList) {
                        genres += gen.text() + ",";
                    }
                } else {
                    genres = "~";
                }
            } else if (i == 9) {
                //status
                status = e.get(i).select("td").text();
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

        cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).update(Manga.class, values, "mMangaUrl = ?", url);
        Manga manga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class).withSelection("mMangaUrl = ?", url).get();
        return manga;
    }
}