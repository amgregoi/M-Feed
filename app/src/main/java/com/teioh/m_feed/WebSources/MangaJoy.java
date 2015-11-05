package com.teioh.m_feed.WebSources;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

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

    /*
     * builds list of chapters for manga object
     */

    public static Observable<List<Chapter>> getChapterListObservable(String url) {
        return pullChaptersFromWebsite(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .onErrorReturn(new Func1<Throwable, List<Chapter>>() {
                    @Override
                    public List<Chapter> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
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

    private static List<Chapter> parseHtmlToChapters(String unparsedHtml) {
        int beginIndex = unparsedHtml.indexOf("<ul class=\"chp_lst\">");
        int endIndex = unparsedHtml.indexOf("</ul>", beginIndex);
        String chapterListHtml = unparsedHtml.substring(beginIndex, endIndex);
        Document parsedDocument = Jsoup.parse(chapterListHtml);
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument);
        return chapterList;
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(Document parsedDocument) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();

        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
            String chapterTitle = chapterElement.select("span").first().text();
            String chapterDate = chapterElement.select("span").get(1).text();

            Chapter curChapter = new Chapter(chapterUrl, chapterTitle, chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
        }
        return chapterList;
    }


    /*
     * builds list of manga from recently updated page
     */
    // pulls manga from lastest updates page
    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        return pullUpdatedMangaFromWebsite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .onErrorReturn(throwable -> {
                    Log.e("yup", "mangajoy");
                    Log.e("throwable", throwable.toString());
                    return null;
                });
    }

    final static String MangaJoyUrl = "http://manga-joy.com/latest-chapters/";

    private static Observable<List<Manga>> pullUpdatedMangaFromWebsite() {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(MangaJoyUrl)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if (connect.execute().statusCode() == 200) {
                        unparsedHtml = connect.get().html().toString();
                    }

                    subscriber.onNext(parseRecentUpdatesToManga(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // takes out the section of html we want to look at
    private static List<Manga> parseRecentUpdatesToManga(String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.wpm_pag.mng_lts_chp.grp");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    // scrapes the title, and querys the database for the object to return to view
    private static List<Manga> scrapeUpdatestoManga(Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("div.row");

        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("div.det.sts_1");
            for (Element usefulElement : usefulElements) {
                String mangaTitle = usefulElement.select("a").attr("Title");
                String today = usefulElement.select("b.dte").first().text();
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("mTitle = ?", mangaTitle).get();
                if (manga != null) {
                    mangaList.add(manga);
                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        return mangaList;
    }


    /*
 * MangaReaderFragment - takes a chapter url, and returns list of urls to chapter images
 */
    public static Observable<List<String>> getChapterImageListObservable(final String url) {
        return temp2(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<String>>() {
                    @Override
                    public List<String> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
    }


    //TODO rename below functions
    private static Observable<List<String>> temp2(final String url) {
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

                    subscriber.onNext(buildImageUrlList2(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<String> buildImageUrlList2(final String unparsedHtml) {
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
        return buildImageUrlList3(directoryHtml);
    }

    private static List<String> buildImageUrlList3(final String unparsedHtml) {
        List<String> imageUrls = new ArrayList<>();
        String prefix = "http://manga-joy.com";
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdate = parsedDocumentForImage.select("a");
        int i = 0;
        for(Element e : imageUpdate)
        {
            if(i > 4) {
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
     * next
     */

    //TODO pull other information, artist, genre, description etc..
    //TODO add new manga
}
