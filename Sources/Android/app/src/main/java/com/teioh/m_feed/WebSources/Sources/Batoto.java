package com.teioh.m_feed.WebSources.Sources;

import com.squareup.okhttp.Headers;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Batoto extends SourceBase
{
    final public static String TAG = Batoto.class.getSimpleName();
    final public static String SourceKey = "Batoto"; //SourceBase.FilterEnum.Batoto.toString();

    final private static String mBaseUrl = "http://bato.to/";
    final private static String mUpdatesUrl = "http://bato.to/search_ajax?order_cond=update&order=desc&p="; //add page number 1,2,3...

    /**
     * builds list of manga for recently updated page
     */
    @Override
    public Observable<List<Manga>> getRecentUpdatesObservable()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        List<Manga> lReturn = new ArrayList<>();
        NetworkService lCurService = NetworkService.getTemporaryInstance();

        return lCurService.getResponseCustomHeaders(mUpdatesUrl + 1, constructRequestHeaders())
                          .flatMap(response -> NetworkService.mapResponseToString(response))
                          .flatMap(html -> Observable.just(scrapeUpdatestoManga(lReturn, html)))
                          .flatMap(list -> lCurService.getResponse(mUpdatesUrl + 2)
                                                      .flatMap(response -> NetworkService.mapResponseToString(response))
                                                      .flatMap(html -> Observable.just(scrapeUpdatestoManga(list, html))))
                          .flatMap(list -> lCurService.getResponse(mUpdatesUrl + 3)
                                                      .flatMap(response -> NetworkService.mapResponseToString(response))
                                                      .flatMap(html -> Observable.just(scrapeUpdatestoManga(list, html))))
                          .observeOn(AndroidSchedulers.mainThread())
                          .retry(5)
                          .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                          .onErrorReturn(null);

    }

    /***
     * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
     *
     * @param request
     * @return
     */
    @Override
    public Observable<String> getChapterImageListObservable(final RequestWrapper request)
    {
        final List<String> temporaryCachedImageUrls = new ArrayList<>();

        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService.getResponseCustomHeaders(request.getChapterUrl(), constructRequestHeaders())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml)))
                             .flatMap(pageUrls -> Observable.from(pageUrls.toArray(new String[pageUrls.size()])))
                             .buffer(5)
                             .concatMap(batchedPageUrls -> {
                                 List<Observable<String>> imageUrlObservables = new ArrayList<>();
                                 for (String pageUrl : batchedPageUrls)
                                 {
                                     Observable<String> temporaryObservable = currentService.getResponseCustomHeaders(pageUrl, constructRequestHeaders())
                                                                                            .flatMap(response -> NetworkService.mapResponseToString(response))
                                                                                            .flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml)))
                                                                                            .subscribeOn(Schedulers.io());

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
     * TODO...
     *
     * @param request
     * @return
     */
    @Override
    public Observable<List<Chapter>> getChapterListObservable(final RequestWrapper request)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        return NetworkService.getPermanentInstance()
                             .getResponseCustomHeaders(request.getMangaUrl(), constructRequestHeaders())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(unparsedHtml -> Observable.just(parseHtmlToChapters(request, unparsedHtml)))
                             .observeOn(AndroidSchedulers.mainThread())
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                             .onErrorReturn(null);

    }

    /***
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @param request
     * @return
     */
    @Override
    public Observable<Manga> updateMangaObservable(final RequestWrapper request)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        String mangaId = request.getMangaUrl().substring(request.getMangaUrl().lastIndexOf("r") + 1);

        return NetworkService.getPermanentInstance()
                             .getResponseCustomHeaders("http://bato.to/comic_pop?id=" + mangaId, constructRequestHeaders())
                             .flatMap(response -> NetworkService.mapResponseToString(response))
                             .flatMap(unparsedHtml -> Observable.just(scrapeAndUpdateManga(request, unparsedHtml)))
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                             .onErrorReturn(null);

    }

    /***
     * TODO...
     *
     * @param aList
     * @param aHtml
     */
    public List<Manga> scrapeUpdatestoManga(List<Manga> aList, String aHtml)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        if (!aHtml.contains("No (more) comics found!"))
        {
            Document lParseDocuments = Jsoup.parse(aHtml);
            Elements lMangaElements = lParseDocuments.select("tr:not([id]):not([class])");


            for (Element iHtmlBlock : lMangaElements)
            {
                Element lUrlElement = iHtmlBlock.select("a[href^=http://bato.to]").first();
                Element lNameElement = lUrlElement;

                String lMangaUrl = lUrlElement.attr("href");
                String lMangaTitle = lNameElement.text().trim();
                Manga lManga = MFDBHelper.getInstance().getManga(lMangaUrl);


                if (lManga != null)
                {
                    aList.add(lManga);
                }
                else
                {
                    lManga = new Manga(lMangaTitle, lMangaUrl, SourceKey);
                    aList.add(lManga);
                    MFDBHelper.getInstance().putManga(lManga);
                    updateMangaObservable(new RequestWrapper(lManga)).subscribeOn(Schedulers.computation())
                                                                     .doOnError(aThrowable -> MangaLogger.logError(TAG, lMethod, aThrowable.getMessage()))
                                                                     .subscribe();
                }
            }
        }

        MangaLogger.logError(TAG, lMethod, " Finished parsing recent updates");

        return aList;
    }

    /***
     * TODO...
     *
     * @param request
     * @param unparsedHtml
     * @return
     */
    private List<Chapter> parseHtmlToChapters(RequestWrapper request, String unparsedHtml)
    {
        List<Chapter> chapterList = new ArrayList<>();

        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements chapterElements = parsedDocument.select("tr.row.lang_English.chapter_row");
        for (Element chapterElement : chapterElements)
        {

            Chapter newChapter = new Chapter();

            Element urlElement = chapterElement.select("a").first();
            Element nameElement = urlElement;
            Element dateElement = chapterElement.select("td").get(4);

            if (urlElement != null)
            {
                String fieldUrl = urlElement.attr("href");
                newChapter.setChapterUrl(fieldUrl);
            }
            if (nameElement != null)
            {
                String fieldName = nameElement.text().trim();
                newChapter.setChapterTitle(fieldName);
            }
            if (dateElement != null)
            {
                try
                {
                    long date = new SimpleDateFormat("dd MMMMM yyyy - hh:mm a", Locale.ENGLISH).parse(dateElement.text()).getTime();
                    newChapter.setChapterDate(new Date(date).toString());
                }
                catch (ParseException e)
                {

                }
            }

            newChapter.setMangaTitle(request.getMangaTitle());
            chapterList.add(newChapter);
        }

        Collections.reverse(chapterList);
        for (int i = 0; i < chapterList.size(); i++)
        {
            chapterList.get(i).setChapterNumber(i + 1);
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
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        List<String> pageUrlList = new ArrayList<>();

        Elements pageUrlElements = parsedDocument.getElementById("page_select").getElementsByTag("option");
        for (Element pageUrlElement : pageUrlElements)
        {
            pageUrlList.add(pageUrlElement.attr("value"));
        }

        return pageUrlList;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private String parseHtmlToImageUrl(String unparsedHtml)
    {
        int beginIndex = unparsedHtml.indexOf("<img id=\"comic_page\"");
        int endIndex = unparsedHtml.indexOf("</a>", beginIndex);
        String trimmedHtml = unparsedHtml.substring(beginIndex, endIndex);

        Document parsedDocument = Jsoup.parse(trimmedHtml);

        Element imageElement = parsedDocument.getElementById("comic_page");

        return imageElement.attr("src");
    }

    /***
     * TODO...
     *
     * @param request
     * @param unparsedHtml
     * @return
     */
    private Manga scrapeAndUpdateManga(RequestWrapper request, String unparsedHtml)
    {
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        Element artistElement = parsedDocument.select("a[href^=http://bato.to/search?artist_name]").first();
        Element descriptionElement = parsedDocument.select("tr").get(5);
        Elements genreElements = parsedDocument.select("img[src=http://bato.to/forums/public/style_images/master/bullet_black.png]");
        Element thumbnailUrlElement = parsedDocument.select("img[src^=http://img.bato.to/forums/uploads/]").first();

        Manga newManga = MFDBHelper.getInstance().getManga(request.getMangaUrl());

        if (newManga == null) newManga = new Manga(request.getMangaTitle(), request.getMangaUrl(), SourceKey);

        if (artistElement != null)
        {
            String fieldArtist = artistElement.text();
            newManga.setArtist(fieldArtist);
            newManga.setAuthor(fieldArtist);
        }
        if (descriptionElement != null)
        {
            String fieldDescription = descriptionElement.text().substring("Description:".length()).trim();
            newManga.setDescription(fieldDescription);
        }
        if (genreElements != null)
        {
            String fieldGenres = "";
            for (int index = 0; index < genreElements.size(); index++)
            {
                String currentGenre = genreElements.get(index).attr("alt");

                if (index < genreElements.size() - 1)
                {
                    fieldGenres += currentGenre + ", ";
                }
                else
                {
                    fieldGenres += currentGenre;
                }
            }
            newManga.setmGenre(fieldGenres);
        }
        if (thumbnailUrlElement != null)
        {
            String fieldThumbnailUrl = thumbnailUrlElement.attr("src");
            newManga.setPicUrl(fieldThumbnailUrl);
        }

        boolean fieldCompleted = unparsedHtml.contains("<td>Complete</td>");
        if (fieldCompleted) newManga.setStatus("Complete");
        else newManga.setStatus("Ongoing");

        newManga.setInitialized(1);

        MFDBHelper.getInstance().putManga(newManga);
        return newManga;

    }

    /***
     * TODO...
     *
     * @return
     */
    private Headers constructRequestHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)");
        headerBuilder.add("Cookie", "lang_option=English");

        return headerBuilder.build();
    }

}