package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

import org.jsoup.nodes.Document;

import java.util.List;

import rx.Observable;

public interface SourceInterface {

    /*
     * builds list of manga for recently updated page
     */
    public Observable<List<Manga>> getRecentUpdatesObservable();

    public Observable<List<Manga>> pullUpdatedMangaFromWebsite();

    public List<Manga> parseRecentUpdatesToManga(final String unparsedHtml);

    public List<Manga> scrapeUpdatestoManga(final Document parsedDocument);

    /*
     * builds list of chapters for manga object
     */

    public Observable<List<Chapter>> getChapterListObservable(final String url);

    public Observable<List<Chapter>> pullChaptersFromWebsite(final String url);

    public List<Chapter> parseHtmlToChapters(final String unparsedHtml);

    public List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument);


    /*
    * ChapterReaderFragment - takes a chapter url, and returns list of urls to chapter images
    */
    public Observable<List<String>> getChapterImageListObservable(final String url);

    public Observable<List<String>> parseListOfImageUrls(final String url);

    public List<String> getBaseUrlDirectory(final String unparsedHtml);

    public List<String> buildImageUrlList(final String unparsedHtml);

    /*
    * Adds new Manga and
    * gets missing manga information and updates database
    */
    public Observable<Manga> updateMangaObservable(final Manga m);

    public Observable<Manga> getUnparsedHtml(final Manga m);

    public Manga scrapeAndUpdateManga(final String unparsedHtml, String url);
}
