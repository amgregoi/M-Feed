package com.teioh.m_feed;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Pojo.Chapter;
import com.teioh.m_feed.Pojo.Manga;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaJoy {

    /*
     * builds list of chapters for manga object
     */
    // requests page containing manga information
    // we are specifically after the lis of chapters
    public Observable<List<Chapter>> pullChaptersFromWebsite(final String url) {
        return Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
            @Override
            public void call(Subscriber<? super List<Chapter>> subscriber) {
                try {
                    Connection connect = Jsoup.connect(url.toLowerCase())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if(connect.execute().statusCode()==200)
                        unparsedHtml = connect.get().html().toString();

                    subscriber.onNext(parseHtmlToChapters(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // takes the requested page and gets rid of the extraneous
    // html that we are not interested in
    private List<Chapter> parseHtmlToChapters(String unparsedHtml){
        int beginIndex = unparsedHtml.indexOf("<ul class=\"chp_lst\">");
        int endIndex = unparsedHtml.indexOf("</ul>", beginIndex);
        String chapterListHtml = unparsedHtml.substring(beginIndex, endIndex);
        Document parsedDocument = Jsoup.parse(chapterListHtml);
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument);
        return chapterList;
    }

    // takes the document that we cleaned up, and gets each chapter from the list
    // building a list chapters to return
    private List<Chapter> scrapeChaptersFromParsedDocument(Document parsedDocument) {
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
    public Observable<List<Manga>> pullUpdatedMangaFromWebsite() {
        return Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try {
                    Connection connect = Jsoup.connect("http://manga-joy.com/latest-chapters/")
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(10000);

                    String unparsedHtml = null;
                    if(connect.execute().statusCode()==200)
                        unparsedHtml = connect.get().html().toString();

                    subscriber.onNext(parseRecentUpdatesToManga(unparsedHtml));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // takes out the section of html we want to look at
    private List<Manga> parseRecentUpdatesToManga(String unparsedHtml){
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.wpm_pag.mng_lts_chp.grp");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    // scrapes the title, and querys the database for the object to return to view
    private List<Manga> scrapeUpdatestoManga(Document parsedDocument) {
        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("div.row");

        SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("div.det.sts_1");
            for(Element usefulElement  : usefulElements)
            {
                String mangaTitle = usefulElement.select("a").attr("Title");
                String today = usefulElement.select("b.dte").first().text();
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("mTitle = ?", mangaTitle).get();
                if(manga != null) {
                    Log.e("manga", manga.getTitle());
                    mangaList.add(manga);
                }
            }
        }
        return mangaList;
    }


    /*
     * next
     */

    //TODO pull other information, artist, genre, description etc..
    //TODO add new manga
}
