package com.teioh.m_feed;

import android.util.Log;

import com.teioh.m_feed.Pojo.Chapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class MangaJoy {

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

    //TODO pull other information, artist, genre, description etc..

    public Observable<List<String>> pullChaptersFromWebsite() {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
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

    private List<String> parseRecentUpdatesToManga(String unparsedHtml){
        int beginIndex = unparsedHtml.indexOf("<div class=\"wpm_pag mng_lts_chp grp\">");
        int endIndex = unparsedHtml.indexOf("/div", beginIndex);
        String chapterListHtml = unparsedHtml.substring(beginIndex, endIndex);
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        Elements updates = parsedDocument.select("div.wpm_pag.mng_lts_chp.grp");


        parsedDocument = Jsoup.parse(updates.toString());
        List<String> chapterList = scrapeUpdatestoManga(parsedDocument);
        return chapterList;
    }

    private List<String> scrapeUpdatestoManga(Document parsedDocument) {
        List<String> chapterList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("div.row");


        for (Element wholeElement : mangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("div.det.sts_1");
            for(Element usefulElement  : usefulElements)
            {
                String mangaTitle = usefulElement.select("a").attr("Title");
                String today = usefulElement.select("b.dte").first().text();
                chapterList.add(mangaTitle);
            }
            if(chapterList.size() > 20) break;  //limits recent to last 21
        }
        return chapterList;
    }


}
