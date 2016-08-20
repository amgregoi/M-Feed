package com.teioh.m_feed.WebSources.Source;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;

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
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaHere {

    final public static String SourceKey = "MangaHere";

    final static String mBaseUrl = "http://mangahere.co/";
    final static String mUpdatesUrl = "http://mangahere.co/latest/";

    /**
     * builds list of manga for recently updated page
     *
     * @return
     */
    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        return NetworkService.getTemporaryInstance()
                .getResponse(mUpdatesUrl)
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(parseRecentUpdatesToManga(html)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(Throwable::printStackTrace);
    }


    private static List<Manga> parseRecentUpdatesToManga(final String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.manga_updates");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> mangaList = scrapeUpdatestoManga(parsedDocument);
        return mangaList;
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
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("title = ? AND source = ?", mangaTitle, SourceKey).get();
                if (manga != null) {
                    mangaList.add(manga);
                } else {
                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    mangaList.add(manga);
                    cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
                    Observable<Manga> observableManga = MangaHere.updateMangaObservable(manga);
                    observableManga.subscribe();
                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        if (mangaList.size() == 0) return null;
        return mangaList;
    }

    /**
     * builds list of chapters for manga object
     *
     * @return
     */

    public static Observable<List<Chapter>> getChapterListObservable(RequestWrapper request) {
        return pullChaptersFromWebsite(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(throwable -> throwable.printStackTrace());
    }

    private static Observable<List<Chapter>> pullChaptersFromWebsite(RequestWrapper request) {
        return Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
            @Override
            public void call(Subscriber<? super List<Chapter>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(request.getMangaUrl().toLowerCase())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200)
                        unparsedHtml = connect.get().html().toString();

                    subscriber.onNext(parseHtmlToChapters(unparsedHtml, request.getMangaTitle()));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Chapter> parseHtmlToChapters(final String unparsedHtml, final String title) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.detail_list").select("ul").not("ul.tab_comment.clearfix");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument, title);
        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument, final String title) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();

        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
//            String title = chapterElement.select("a").text();
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
     *
     * @return
     */
    public static Observable<String> getChapterImageList(final RequestWrapper request) {
        final List<String> temporaryCachedImageUrls = new ArrayList<>();

        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService
                .getResponse(request.getChapterUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml)))
                .flatMap(pageUrls -> Observable.from(pageUrls.toArray(new String[pageUrls.size()])))
                .buffer(10)
                .concatMap(batchedPageUrls -> {
                    List<Observable<String>> imageUrlObservables = new ArrayList<>();
                    for (String pageUrl : batchedPageUrls) {
                        Observable<String> temporaryObservable = currentService
                                .getResponse(pageUrl)
                                .subscribeOn(Schedulers.io())
                                .flatMap(response -> NetworkService.mapResponseToString(response))
                                .flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml)));
                        imageUrlObservables.add(temporaryObservable);
                    }

                    return Observable.zip(imageUrlObservables, args -> {
                        List<String> imageUrls = new ArrayList<>();
                        for (Object uncastImageUrl : args) {
                            imageUrls.add(String.valueOf(uncastImageUrl));
                        }
                        return imageUrls;
                    });
                })
                .concatMap(batchedImageUrls -> Observable.from(batchedImageUrls.toArray(new String[batchedImageUrls.size()])))
                .doOnNext(imageUrl -> temporaryCachedImageUrls.add(imageUrl))
                .onBackpressureBuffer();

    }

    private static List<String> parseHtmlToPageUrls(String unparsedHtml) {
        List<String> pageUrls = new ArrayList<>();

        //get base url for images
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdates = parsedDocumentForImage.select("select.wid60").first().select("option");
        for (Element url : imageUpdates) {
            pageUrls.add(url.attr("value"));
        }
        return pageUrls;
    }

    private static String parseHtmlToImageUrl(String unparsedHtml) {
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        String url = parsedDocumentForImage.select("section#viewer.read_img").select("img#image").attr("src");

        return url;
    }


    /**
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @return
     */
    public static Observable<Manga> updateMangaObservable(final Manga m) {
        return getUnparsedHtml(m)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .onErrorReturn(throwable -> {
                    Log.e("throwable", throwable.toString());
                    return null;
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
            } else if (i == 6) {
                status = e.get(i).text().replace(e.get(i).select("label").text(), "");
            } else if (i == 8) {
                summary = e.get(i).text();
            }
        }


        ContentValues values = new ContentValues(1);
        values.put("alternate", alternate);
        values.put("image", img);
        values.put("description", summary);
        values.put("artist", artist);
        values.put("author", author);
        values.put("genres", genres);
        values.put("status", status);
        values.put("source", SourceKey);

        cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getWritableDatabase())
                .update(Manga.class, values, "link = ?", url);

        return cupboard().withDatabase(MangaFeedDbHelper.getInstance()
                .getReadableDatabase()).query(Manga.class)
                .withSelection("link = ? AND source = ?", url, SourceKey).get();
    }
}