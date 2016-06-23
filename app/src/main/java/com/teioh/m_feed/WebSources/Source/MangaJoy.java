package com.teioh.m_feed.WebSources.Source;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/*
 *
 * Site is throwing 500 errors, pretty sure its on their end
 *
 *
 */
public class MangaJoy {

    final public static String SourceKey = "MangaJoy";

    final static String mBaseUrl = "http://manga-joy.com/";
    final static String mUpdatesUrl = "http://manga-joy.com/latest-chapters";

    final public static String genres[] = {
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
            "Yuri"
    };

    /*
     * builds list of manga for recently updated page
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
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("title = ? AND source = ?", mangaTitle, SourceKey).get();
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
        if (mangaList.size() == 0) return null;
        return mangaList;
    }

    /*
     * builds list of chapters for manga object
     */

    public static Observable<List<Chapter>> getChapterListObservable(RequestWrapper request) {
        NetworkService currService = NetworkService.getTemporaryInstance();

        return currService
                .getResponse(request.getMangaUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(parseHtmlToChapters(html, request.getMangaTitle())))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> throwable.printStackTrace());
    }

    private static List<Chapter> parseHtmlToChapters(final String unparsedHtml, final String title) {
        int beginIndex = unparsedHtml.indexOf("<ul class=\"chp_lst\">");
        int endIndex = unparsedHtml.indexOf("</ul>", beginIndex);
        String chapterListHtml = unparsedHtml.substring(beginIndex, endIndex);
        Document parsedDocument = Jsoup.parse(chapterListHtml);
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument, title);
        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument, final String title) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();


        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
            String[] cTitle = chapterElement.select("span").first().text().split(":");

            String chapterDate = chapterElement.select("span").get(1).text();

            Chapter curChapter = new Chapter(chapterUrl, title, cTitle[0].trim(), chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
        }
        return chapterList;
    }

    public static Observable<String> getChapterImageList(final RequestWrapper request) {
        final List<String> temporaryCachedImageUrls = new ArrayList<String>();
        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService
                .getResponse(request.getChapterUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml, request.getChapterUrl())))
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


    private static String parseHtmlToImageUrl(String html) {
        Document parsedDocument = Jsoup.parse(html);
        String link = parsedDocument.select("div[class=prw]").first().select("img").attr("src");
        return link;
    }

    private static List<String> parseHtmlToPageUrls(String html, String url) {
        List<String> images = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        Elements nav = doc
                .select("div[class=wpm_nav_rdr]")
                .first()
                .select("select[onchange^=location.href='" + url
                        + "' + this.value + '/']").first().select("option");

        int pages = nav.size();

        String referrer = url;
        for (int i = 1; i <= pages; i++) {
            if (i != 1) {
                referrer = url + (i) + "/";
            }

            String link = doc.select("div[class=prw]").first().select("img").attr("src");

            images.add(referrer);
        }
        return images;
    }

    /*
    * Adds new Manga and
    * gets missing manga information and updates database
    */
    public static Observable<Manga> updateMangaObservable(final Manga m) {
        NetworkService curService = NetworkService.getTemporaryInstance();

        return curService.getResponse(m.getMangaURL())
                .flatMap(response -> curService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeAndUpdateManga(html, m.getMangaURL())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> throwable.printStackTrace());
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



