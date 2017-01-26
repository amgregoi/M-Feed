package com.teioh.m_feed.WebSources.Sources;

import android.content.ContentValues;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MFDBHelper;
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
        String lMethod = Thread.currentThread()
                               .getStackTrace()[2].getMethodName();

        return NetworkService.getTemporaryInstance()
                             .getResponse(mUpdatesUrl)
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(html -> Observable.just(parseRecentUpdatesToManga(html)))
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
        final List<String> temporaryCachedImageUrls = new ArrayList<>();

        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService.getResponse(request.getChapterUrl())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml)))
                             .flatMap(pageUrls -> Observable.from(pageUrls.toArray(new String[pageUrls.size()])))
                             .buffer(10)
                             .concatMap(batchedPageUrls -> {
                                 List<Observable<String>> imageUrlObservables = new ArrayList<>();
                                 for (String pageUrl : batchedPageUrls)
                                 {
                                     Observable<String> temporaryObservable = currentService.getResponse(pageUrl)
                                                                                            .subscribeOn(Schedulers.io())
                                                                                            .flatMap(response -> NetworkService.mapResponseToString(response))
                                                                                            .flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml)));
                                     imageUrlObservables.add(temporaryObservable);
                                 }

                                 return Observable.zip(imageUrlObservables, args -> {
                                     List<String> imageUrls = new ArrayList<>();
                                     for (Object uncastImageUrl : args)
                                     {
                                         imageUrls.add(String.valueOf(uncastImageUrl));
                                     }
                                     return imageUrls;
                                 });
                             })
                             .concatMap(batchedImageUrls -> Observable.from(batchedImageUrls.toArray(new String[batchedImageUrls.size()])))
                             .doOnNext(imageUrl -> temporaryCachedImageUrls.add(imageUrl))
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
        String lMethod = Thread.currentThread()
                               .getStackTrace()[2].getMethodName();

        return NetworkService.getTemporaryInstance()
                             .getResponse(request.getMangaUrl())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(html -> Observable.just(parseHtmlToChapters(request, html)))
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
        String lMethod = Thread.currentThread()
                               .getStackTrace()[2].getMethodName();
        NetworkService lCurService = NetworkService.getTemporaryInstance();

        return lCurService.getResponse(aRequest.getMangaUrl())
                          .flatMap(response -> lCurService.mapResponseToString(response))
                          .flatMap(html -> Observable.just(scrapeAndUpdateManga(html, aRequest.getMangaUrl())))
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
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.manga_updates");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Manga> mangaList = scrapeUpdatestoManga(parsedDocument);
        return mangaList;
    }

    /***
     * TODO...
     *
     * @param parsedDocument
     * @return
     */
    private List<Manga> scrapeUpdatestoManga(final Document parsedDocument)
    {
        String lMethod = Thread.currentThread()
                               .getStackTrace()[2].getMethodName();

        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("dl");

        for (Element wholeElement : mangaElements)
        {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("dt");
            for (Element usefulElement : usefulElements)
            {
                String mangaTitle = usefulElement.select("a")
                                                 .attr("rel");
                String mangaUrl = usefulElement.select("a")
                                               .attr("href");
                Manga lManga = MFDBHelper.getInstance()
                                         .getManga(mangaTitle, SourceKey);
                if (lManga != null)
                {
                    mangaList.add(lManga);
                }
                else
                {
                    lManga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    mangaList.add(lManga);
                    MFDBHelper.getInstance()
                              .putManga(lManga);
                    updateMangaObservable(new RequestWrapper(lManga)).subscribeOn(Schedulers.computation())
                                                                     .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                                                                     .subscribe();
                }
            }
        }
        MangaLogger.logError(TAG, lMethod, " Finished parsing recent updates");
        if (mangaList.size() == 0) return null;
        return mangaList;
    }

    /***
     * TODO...
     *
     * @param aRequest
     * @param unparsedHtml
     * @return
     */
    private List<Chapter> parseHtmlToChapters(RequestWrapper aRequest, String unparsedHtml)
    {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div.detail_list")
                                         .select("ul")
                                         .not("ul.tab_comment.clearfix");
        parsedDocument = Jsoup.parse(updates.toString());
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument, aRequest.getMangaTitle());
        return chapterList;
    }

    /***
     * TODO...
     *
     * @param parsedDocument
     * @param title
     * @return
     */
    private List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument, final String title)
    {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.getElementsByTag("li");
        int numChapters = chapterElements.size();

        for (Element chapterElement : chapterElements)
        {
            String chapterUrl = chapterElement.select("a")
                                              .attr("href");
//            String title = chapterElement.select("a").text();
            String cTitle = chapterElement.select("span.left")
                                          .text();
            String chapterDate = chapterElement.select("span.right")
                                               .text();

            Chapter curChapter = new Chapter(chapterUrl, title, cTitle, chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
        }
        return chapterList;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private List<String> parseHtmlToPageUrls(String unparsedHtml)
    {
        List<String> pageUrls = new ArrayList<>();

        //get base url for images
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdates = parsedDocumentForImage.select("select.wid60")
                                                      .first()
                                                      .select("option");
        for (Element url : imageUpdates)
        {
            pageUrls.add(url.attr("value"));
        }
        return pageUrls;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private String parseHtmlToImageUrl(String unparsedHtml)
    {
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        String url = parsedDocumentForImage.select("section#viewer.read_img")
                                           .select("img#image")
                                           .attr("src");

        return url;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @param url
     * @return
     */
    private Manga scrapeAndUpdateManga(final String unparsedHtml, String url)
    {
        Document html = Jsoup.parse(unparsedHtml);
        Elements usefulSection = html.select("div.manga_detail_top.clearfix");

        //image url
        Element imageElement = usefulSection.select("img")
                                            .first();
        //summary
        Elements e = usefulSection.select("ul.detail_topText")
                                  .select("li");


        String img = imageElement.attr("src");
        String summary = null;
        String alternate = null;
        String author = null;
        String artist = null;
        String genres = null;
        String status = null;
        for (int i = 0; i < e.size(); i++)
        {
            if (i == 2)
            {
                alternate = e.get(i)
                             .text()
                             .replace(e.get(i)
                                       .select("label")
                                       .text(), "");
            }
            else if (i == 4)
            {
                author = e.get(i)
                          .text()
                          .replace(e.get(i)
                                    .select("label")
                                    .text(), "");
            }
            else if (i == 5)
            {
                artist = e.get(i)
                          .text()
                          .replace(e.get(i)
                                    .select("label")
                                    .text(), "");
            }
            else if (i == 3)
            {
                genres = e.get(i)
                          .text()
                          .replace(e.get(i)
                                    .select("label")
                                    .text(), "");
            }
            else if (i == 6)
            {
                status = e.get(i)
                          .text()
                          .replace(e.get(i)
                                    .select("label")
                                    .text(), "");
            }
            else if (i == 8)
            {
                summary = e.get(i)
                           .text();
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

        MFDBHelper.getInstance()
                  .updateManga(values, url);
        return MFDBHelper.getInstance()
                         .getManga(url, SourceKey);
    }
}