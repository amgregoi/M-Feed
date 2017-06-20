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

public class MangaJoy extends SourceBase
{
    final public static String TAG = MangaJoy.class.getSimpleName();

    final public static String SourceKey = "MangaJoy";
    final public static String genres[] = {"Action", "Adult", "Adventure", "Comedy", "Doujinshi", "Drama", "Ecchi", "Fantasy", "Gender Bender", "Harem", "Historical", "Horror", "Josei", "Lolicon", "Manga", "Manhua", "Manhwa", "Martial Arts", "Mature", "Mecha", "Mystery", "One shot", "Psychological", "Romance", "School Life", "Sci fi", "Seinen", "Shotacon", "Shoujo", "Shoujo Ai", "Shounen", "Shounen Ai", "Slice of Life", "Smut", "Sports", "Supernatural", "Tragedy", "Yaoi", "Yuri"};
    final static String mBaseUrl = "http://funmanga.com/";
    final static String mUpdatesUrl = "http://funmanga.com/latest-chapters";

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
                             .flatMap(aHtml -> Observable.just(scrapeUpdatestoManga(aHtml)))
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .retry(3)
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

    /***
     * TODO...
     *
     * @param aWrapper
     * @return
     */
    public Observable<String> getChapterImageListObservable(final RequestWrapper aWrapper)
    {
        final List<String> lTemporaryCachedImageUrls = new ArrayList<>();
        final NetworkService lCurrentService = NetworkService.getTemporaryInstance();

        return lCurrentService.getResponse(aWrapper.getChapterUrl())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aUnparsedHtml -> Observable.just(parseHtmlToPageUrls(aUnparsedHtml)))
                             .flatMap(aPageUrls -> Observable.from(aPageUrls.toArray(new String[aPageUrls.size()])))
                             .buffer(10)
                             .concatMap(batchedPageUrls ->
                                        {
                                            List<Observable<String>> lImageUrlObservables = new ArrayList<>();
                                            for (String iPageUrl : batchedPageUrls)
                                            {
                                                Observable<String> lTemporaryObservable = lCurrentService.getResponse(iPageUrl)
                                                                                                       .subscribeOn(Schedulers.io())
                                                                                                       .flatMap(aResponse -> NetworkService
                                                                                                               .mapResponseToString(aResponse))
                                                                                                       .flatMap(unparsedHtml -> Observable
                                                                                                               .just(parseHtmlToImageUrl(unparsedHtml)));
                                                lImageUrlObservables.add(lTemporaryObservable);
                                            }

                                            return Observable.zip(lImageUrlObservables, args ->
                                            {
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
     * @param request
     * @return
     */
    public Observable<List<Chapter>> getChapterListObservable(RequestWrapper request)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        MangaLogger.logInfo(TAG, lMethod, "Entering");

        return NetworkService.getTemporaryInstance()
                             .getResponse(request.getMangaUrl())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(html -> Observable.just(parseHtmlToChapters(request, html)))
                             .observeOn(AndroidSchedulers.mainThread());
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
        NetworkService lCurService = NetworkService.getTemporaryInstance();

        return lCurService.getResponse(aRequest.getMangaUrl())
                         .flatMap(response -> lCurService.mapResponseToString(response))
                         .flatMap(html -> Observable.just(scrapeAndUpdateManga(html, aRequest)))
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread());
    }

    /***
     * TODO...
     *
     * @param aUnparsedHtml
     * @return
     */
    private List<Manga> scrapeUpdatestoManga(final String aUnparsedHtml)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        List<Manga> lMangaList = new ArrayList<>();

        try
        {
            Document lParsedDocument = Jsoup.parse(aUnparsedHtml);
            Elements lMangaElements = lParsedDocument.select("div.manga_updates").select("dl");

            for (Element iWholeElement : lMangaElements)
            {
                Document lParseSections = Jsoup.parse(iWholeElement.toString());
                Elements lUsefulElements = lParseSections.select("dt");
                for (Element iUsefulElement : lUsefulElements)
                {
                    String lMangaTitle = iUsefulElement.select("a").attr("title");
                    String lMangaUrl = iUsefulElement.select("a").attr("href");

                    if (lMangaUrl.charAt(lMangaUrl.length() - 1) != '/') lMangaUrl += "/"; //add ending slash to url if missing
                    Manga lManga = MangaDB.getInstance().getManga(lMangaUrl);
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
                                                                         .doOnError(aThrowable -> MangaLogger
                                                                                 .logError(TAG, lMethod, aThrowable.getMessage()))
                                                                         .onErrorReturn(null)
                                                                         .subscribe();
                    }
                }
            }

            MangaLogger.logInfo(TAG, lMethod, "Finished parsing recent updates");
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, " Failed to parse recent updates: ");
        }

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
        Elements lChapterElements = aParsedDocument.select("ul.chapter-list").select("li");
        int lNumChapters = lChapterElements.size();

        String lChapterUrl, lChapterTitle, lChapterDate;

        for (Element iChapterElement : lChapterElements)
        {
            lChapterUrl = iChapterElement.select("a").attr("href");
            lChapterTitle = iChapterElement.select("span").first().text();
            lChapterDate = iChapterElement.select("span").get(1).text();

            lChapterList.add(new Chapter(lChapterUrl, aTitle, lChapterTitle, lChapterDate, lNumChapters));

            lNumChapters--;
        }

        return lChapterList;
    }

    /***
     * TODO...
     *
     * @param aHtml
     * @return
     */
    public List<String> parseHtmlToPageUrls(String aHtml)
    {
        List<String> lImages = new ArrayList<>();

        Document lDoc = Jsoup.parse(aHtml);

        Elements lNav = lDoc.select("h5.widget-heading").select("select").select("option");

        int lPages = lNav.size();

        for (int i = 1; i < lPages; i++)
        {
            String lLink = lNav.get(i).attr("value");
            lImages.add(lLink);
        }

        return lImages;
    }

    /***
     * TODO...
     *
     * @param aHtml
     * @return
     */
    public String parseHtmlToImageUrl(String aHtml)
    {
        Document lParsedDocument = Jsoup.parse(aHtml);
        String lLink = lParsedDocument.select("img.img-responsive").attr("src");

        return lLink;
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
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        Document lHtml = Jsoup.parse(aUnparsedHtml);

        try
        {
            //image url
            Element lImageElement = lHtml.body().select("img.img-responsive.mobile-img").first();
            //summary
            Element summaryElement = lHtml.body().select("div.note.note-default.margin-top-15").first();

            Elements lInfo = lHtml.body().select("dl.dl-horizontal").select("dd");
            String lImage = lImageElement.attr("src");
            String lSummary = summaryElement.text();
            String lAlternate = null;
            String lAuthor = null;
            String lArtist = null;
            String lGenres = null;
            String lStatus = null;

            for (int i = 0; i < lInfo.size(); i++)
            {
                if (i == 0)
                {
                    lAlternate = lInfo.get(i).text();
                }
                else if (i == 5)
                {
                    lAuthor = lInfo.get(i).text();
                }
                else if (i == 4)
                {
                    lArtist = lInfo.get(i).text();
                }
                else if (i == 2)
                {
                    lGenres = lInfo.get(i).text();
                }
                else if (i == 1)
                {
                    lStatus = lInfo.get(i).text();
                }
            }

            Manga lManga = new Manga();
            lManga.setAlternate(lAlternate);
            lManga.setPicUrl(lImage);
            lManga.setDescription(lSummary);
            lManga.setArtist(lArtist);
            lManga.setAuthor(lAuthor);
            lManga.setmGenre(lGenres);
            lManga.setStatus(lStatus);
            lManga.setSource(SourceKey);
            lManga.setMangaUrl(aRequest.getMangaUrl());

            MangaDB.getInstance().updateManga(lManga);
            return MangaDB.getInstance().getManga(aRequest.getMangaUrl());
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }

        return null;
    }
}



