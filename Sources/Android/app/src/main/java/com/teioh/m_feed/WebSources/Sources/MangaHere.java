package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceBase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MangaHere extends SourceBase
{
    final public static String TAG = MangaHere.class.getSimpleName();
    final public String SourceKey = "MangaHere";

    final String mBaseUrl = "http://mangahere.co/";
    final String mUpdatesUrl = "http://mangahere.co/latest/";

    /***
     * builds list of manga for recently updated page
     *
     * @return
     */
    public Observable<List<Manga>> getRecentUpdatesObservable()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        return NetworkService.getTemporaryInstance()
                             .getResponse(mUpdatesUrl)
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aHtml -> Observable.just(parseRecentUpdatesToManga(aHtml)))
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .retry(5)
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

    /***
     * TODO...
     *
     * @param request
     * @return
     */
    public Observable<String> getChapterImageListObservable(final RequestWrapper request)
    {
        final List<String> lTemporaryCachedImageUrls = new ArrayList<>();

        final NetworkService lCurrentService = NetworkService.getTemporaryInstance();

        return lCurrentService.getResponse(request.getChapterUrl())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aUnparsedHtml -> Observable.just(parseHtmlToPageUrls(aUnparsedHtml)))
                             .flatMap(aPageUrls -> Observable.from(aPageUrls.toArray(new String[aPageUrls.size()])))
                             .buffer(10)
                             .concatMap(aBatchedPageUrls -> {
                                 List<Observable<String>> lImageUrlObservables = new ArrayList<>();
                                 for (String iPageUrl : aBatchedPageUrls)
                                 {
                                     Observable<String> lTemporaryObservable = lCurrentService.getResponse(iPageUrl)
                                                                                            .subscribeOn(Schedulers.io())
                                                                                            .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                                                                                            .flatMap(aUnparsedHtml -> Observable.just(parseHtmlToImageUrl(aUnparsedHtml)));
                                     lImageUrlObservables.add(lTemporaryObservable);
                                 }

                                 return Observable.zip(lImageUrlObservables, args -> {
                                     List<String> lImageUrls = new ArrayList<>();
                                     for (Object iUncastImageUrl : args)
                                     {
                                         lImageUrls.add(String.valueOf(iUncastImageUrl));
                                     }
                                     return lImageUrls;
                                 });
                             })
                             .concatMap(batchedImageUrls -> Observable.from(batchedImageUrls.toArray(new String[batchedImageUrls.size()])))
                             .doOnNext(imageUrl -> lTemporaryCachedImageUrls.add(imageUrl))
                             .onBackpressureBuffer();

    }

    /***
     * builds list of chapters for manga object
     *
     * @param aRequest
     * @return
     */
    public Observable<List<Chapter>> getChapterListObservable(RequestWrapper aRequest)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        return NetworkService.getTemporaryInstance()
                             .getResponse(aRequest.getMangaUrl())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aHtml -> Observable.just(parseHtmlToChapters(aRequest, aHtml)))
                             .observeOn(AndroidSchedulers.mainThread())
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

    /***
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @param aRequest
     * @return
     */
    public Observable<Manga> updateMangaObservable(RequestWrapper aRequest)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        NetworkService lCurService = NetworkService.getTemporaryInstance();

        return lCurService.getResponse(aRequest.getMangaUrl())
                          .flatMap(response -> lCurService.mapResponseToString(response))
                          .flatMap(html -> Observable.just(scrapeAndUpdateManga(html, aRequest)))
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                          .onErrorReturn(null);
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private List<Manga> parseRecentUpdatesToManga(final String unparsedHtml)
    {
        Document lParsedDocument = Jsoup.parse(unparsedHtml);
        Elements lUpdates = lParsedDocument.select("div.manga_updates");
        lParsedDocument = Jsoup.parse(lUpdates.toString());
        List<Manga> lMangaList = scrapeUpdatestoManga(lParsedDocument);

        return lMangaList;
    }

    /***
     * TODO...
     *
     * @param aParsedDocument
     * @return
     */
    private List<Manga> scrapeUpdatestoManga(final Document aParsedDocument)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        List<Manga> lMangaList = new ArrayList<>();
        Elements lMangaElements = aParsedDocument.select("dl");

        for (Element iWholeElement : lMangaElements)
        {
            Document lParseSections = Jsoup.parse(iWholeElement.toString());
            Elements lUsefulElements = lParseSections.select("dt");
            for (Element iUsefulElement : lUsefulElements)
            {
                String lMangaTitle = iUsefulElement.select("a").attr("rel");
                String lMangaUrl = iUsefulElement.select("a").attr("href");
                Manga lManga = MangaDB.getInstance().getManga(lMangaTitle);
                if (lManga != null)
                {
                    lMangaList.add(lManga);
                }
                else
                {
                    lManga = new Manga(lMangaTitle, lMangaUrl, SourceKey);
                    lMangaList.add(lManga);
                    MangaDB.getInstance().putManga(lManga);
                    updateMangaObservable(new RequestWrapper(lManga)).subscribeOn(Schedulers.computation())
                                                                     .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                                                                     .subscribe();
                }
            }
        }
        MangaLogger.logInfo(TAG, lMethod, " Finished parsing recent updates");

        if (lMangaList.size() == 0) return null;
        return lMangaList;
    }

    /***
     * TODO...
     *
     * @param aRequest
     * @param aUnparsedHtml
     * @return
     */
    private List<Chapter> parseHtmlToChapters(RequestWrapper aRequest, String aUnparsedHtml)
    {
        Document lParsedDocument = Jsoup.parse(aUnparsedHtml);
        Elements lUpdates = lParsedDocument.select("div.detail_list").select("ul").not("ul.tab_comment.clearfix");
        lParsedDocument = Jsoup.parse(lUpdates.toString());
        List<Chapter> lChapterList = scrapeChaptersFromParsedDocument(lParsedDocument, aRequest.getMangaTitle());

        return lChapterList;
    }

    /***
     * TODO...
     *
     * @param aParsedDocument
     * @param aTitle
     * @return
     */
    private List<Chapter> scrapeChaptersFromParsedDocument(final Document aParsedDocument, final String aTitle)
    {
        List<Chapter> lChapterList = new ArrayList<>();
        Elements lChapterElements = aParsedDocument.getElementsByTag("li");
        int lNumChapters = lChapterElements.size();

        for (Element iChapterElement : lChapterElements)
        {
            String lChapterUrl = iChapterElement.select("a").attr("href");
            String lChapterTitle = iChapterElement.select("span.left").text();
            String lChapterDate = iChapterElement.select("span.right").text();

            Chapter lCurChapter = new Chapter(lChapterUrl, aTitle, lChapterTitle, lChapterDate, lNumChapters);
            lNumChapters--;

            lChapterList.add(lCurChapter);
        }

        return lChapterList;
    }

    /***
     * TODO...
     *
     * @param aUnparsedHtml
     * @return
     */
    private List<String> parseHtmlToPageUrls(String aUnparsedHtml)
    {
        List<String> lPageUrls = new ArrayList<>();

        //get base url for images
        Document lParsedDocumentForImage = Jsoup.parse(aUnparsedHtml);
        Elements lImageUpdates = lParsedDocumentForImage.select("select.wid60").first().select("option");

        for (Element iUrl : lImageUpdates)
        {
            lPageUrls.add(iUrl.attr("value"));
        }

        return lPageUrls;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private String parseHtmlToImageUrl(String unparsedHtml)
    {
        Document lParsedDocumentForImage = Jsoup.parse(unparsedHtml);
        String lUrl = lParsedDocumentForImage.select("section#viewer.read_img").select("img#image").attr("src");

        return lUrl;
    }

    /***
     * TODO...
     *
     * @param aUnparsedHtml
     * @param aRequest
     * @return
     */
    private Manga scrapeAndUpdateManga(final String aUnparsedHtml, RequestWrapper aRequest)
    {
        Document lHtml = Jsoup.parse(aUnparsedHtml);
        Elements lUsefulSection = lHtml.select("div.manga_detail_top.clearfix");

        //image url
        Element lImageElement = lUsefulSection.select("img").first();
        //summary
        Elements lHeaderInfo = lUsefulSection.select("ul.detail_topText").select("li");


        if(lImageElement != null && lHeaderInfo != null)
        {
            String img = lImageElement.attr("src");
            String summary = null;
            String alternate = null;
            String author = null;
            String artist = null;
            String genres = null;
            String status = null;
            for (int i = 0; i < lHeaderInfo.size(); i++)
            {
                if (i == 2)
                {
                    alternate = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 4)
                {
                    author = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 5)
                {
                    artist = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 3)
                {
                    genres = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 6)
                {
                    status = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 8)
                {
                    summary = lHeaderInfo.get(i).text();
                }
            }

            Manga lManga = new Manga();
            lManga.setAlternate(alternate);
            lManga.setPicUrl(img);
            lManga.setDescription(summary);
            lManga.setArtist(artist);
            lManga.setAuthor(author);
            lManga.setmGenre(genres);
            lManga.setStatus(status);
            lManga.setSource(SourceKey);
            lManga.setMangaUrl(aRequest.getMangaUrl());


            MangaDB.getInstance().updateManga(lManga);
            return MangaDB.getInstance().getManga(aRequest.getMangaUrl());
        }

        return null;
    }
}