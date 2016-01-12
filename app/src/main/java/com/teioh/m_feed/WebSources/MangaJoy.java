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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaJoy {

    final public static String SourceKey = "MangaJoy";

    final static String MangaJoyUrl = "http://manga-joy.com/latest-chapters/";

    final String genres[] = {
            "Action",
            "Adult",
            "Adventure",
            "Comedy",
            "Doujinshi",
            "Drama",
            "Ecchi",
            "Fantasy",
            "Gender Bender",
            "Harem",
            "Historical",
            "Horror",
            "Josei",
            "Lolicon",
            "Manga",
            "Manhua",
            "Manhwa",
            "Martial Arts",
            "Mature",
            "Mecha",
            "Mystery",
            "One shot",
            "Psychological",
            "Romance",
            "School Life",
            "Sci fi",
            "Seinen",
            "Shotacon",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Smut",
            "Sports",
            "Supernatural",
            "Tragedy",
            "Yaoi",
            "Yuri"};

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
                    Connection connect = Jsoup.connect(MangaJoyUrl)
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
        Elements updates = parsedDocument.select("div.wpm_pag.mng_lts_chp.grp");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    private static List<Manga> scrapeUpdatestoManga(final Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("div.row");

        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("div.det.sts_1");
            for (Element usefulElement : usefulElements) {
                String mangaTitle = usefulElement.select("a").attr("Title");
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
        int beginIndex = unparsedHtml.indexOf("<ul class=\"chp_lst\">");
        int endIndex = unparsedHtml.indexOf("</ul>", beginIndex);
        String chapterListHtml = unparsedHtml.substring(beginIndex, endIndex);
        Document parsedDocument = Jsoup.parse(chapterListHtml);
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument);
        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();

        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
            String[] titles = chapterElement.select("span").first().text().split(" : ");

            String chapterDate = chapterElement.select("span").get(1).text();

            Chapter curChapter = new Chapter(chapterUrl, titles[0], titles[1], chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
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
        Elements imageUpdate = parsedDocumentForImage.select("div.prw");
        parsedDocumentForImage = Jsoup.parse(imageUpdate.toString());
        String imageUrl = parsedDocumentForImage.select("img").attr("src");

        //get img extension
        Pattern regex2 = Pattern.compile("\\/(?!.*\\/).*");
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
        return buildImageUrlList(directoryHtml);
    }

    private static List<String> buildImageUrlList(final String unparsedHtml) {
        List<String> imageUrls = new ArrayList<>();
        String prefix = "http://manga-joy.com";
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdate = parsedDocumentForImage.select("a");
        int i = 0;
        for (Element e : imageUpdate) {
            if (i > 4) {
                String postfix = e.select("a").attr("href");
                if (postfix.contains("manga")) {
                    imageUrls.add(prefix + postfix);
                }
            }
            i++;
        }
        return imageUrls;
    }


    /*
    * Adds new Manga and
    * gets missing manga information and updates database
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
                        unparsedHtml = connect.get().html().toString();

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

        //image url
        Element imageElement = html.body().select("img.cvr").first();
        //summary
        Element summaryElement = html.body().select("p.summary").first();

        Elements e = html.body().select("div.det").select("span");
        String img = imageElement.attr("src");
        String summary = summaryElement.text().substring(7);
        String alternate = null;
        String author = null;
        String artist = null;
        String genres = null;
        String status = null;
        for (int i = 0; i < e.size(); i++) {
            if (i == 0) {
                alternate = e.get(i).text();
            } else if (i == 1) {
                author = e.get(i).text();
            } else if (i == 2) {
                artist = e.get(i).text();
            } else if (i == 3) {
                genres = e.get(i).text();
            } else if (i == 5) {
                status = e.get(i).text();
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

        Manga manga = cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getReadableDatabase()).query(Manga.class)
                .withSelection("mMangaUrl = ? AND mSource = ?", url, SourceKey).get();

        return manga;
    }
}