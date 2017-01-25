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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MangaPark extends SourceBase
{
    final public static String TAG = MangaPark.class.getSimpleName();
    final public String SourceKey = "MangaPark";

    final String mUpdateUrl = "http://mangapark.me/latest/";
    final String mBaseUrl = "http://mangapark.me";

    /**
     * builds list of manga for recently updated page
     *
     * @return
     */
    public Observable<List<Manga>> getRecentUpdatesObservable()
    {
        return NetworkService.getTemporaryInstance().getResponse(mUpdateUrl).flatMap(html -> NetworkService.mapResponseToString(html)).flatMap(html -> Observable.just(parseRecentUpdatesToManga(html))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(5).doOnError(Throwable::printStackTrace);
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
        Elements updates = parsedDocument.select("div.item");
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
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        List<Manga> mangaList = new ArrayList<>();
        Elements mangaElements = parsedDocument.select("div.item");

        for (Element wholeElement : mangaElements)
        {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("ul").select("h3");
            for (Element usefulElement : usefulElements)
            {
                String mangaTitle = usefulElement.select("a").text();
                String mangaUrl = mBaseUrl + usefulElement.select("a").attr("href");
                Manga manga = MFDBHelper.getInstance().getManga(mangaUrl, SourceKey);
                if (manga != null)
                {
                    mangaList.add(manga);
                }
                else
                {
                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    MFDBHelper.getInstance().putManga(manga);
                    mangaList.add(manga);
                }
//                else {
//                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
//                    cupboard().withDatabase(MFDBHelper.getInstance().getWritableDatabase()).put(manga);
//                    Observable<Manga> observableManga = MangaPark.updateMangaObservable(manga);
//                    observableManga.subscribe();
//                }
            }
        }
        MangaLogger.logError(TAG, lMethod, " Finished parsing recent updates");
        if (mangaList.size() == 0) return null;
        return mangaList;
    }

    /***
     * builds list of chapters for manga object
     *
     * @param request
     * @return
     */
    public Observable<List<Chapter>> getChapterListObservable(RequestWrapper request)
    {
        NetworkService currService = NetworkService.getTemporaryInstance();

        return currService.getResponse(request.getMangaUrl()).flatMap(response -> NetworkService.mapResponseToString(response)).flatMap(html -> Observable.just(parseHtmlToChapters(request, html))).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> throwable.printStackTrace());

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
        int chosenIndex = 0, count = -1, i = 0;
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements updates = parsedDocument.select("div#list.book-list").select("div.stream");
        for (Element e : updates)
        {
            Elements chapters = e.select("ul.chapter").select("li");
            if (chapters.size() > count)
            {
                count = chapters.size();
                chosenIndex = i;
            }
            i++;
        }
        parsedDocument = Jsoup.parse(updates.get(chosenIndex).toString());
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
        Elements chapterElements = parsedDocument.select("ul.chapter").select("li");
        Elements temp;

        for (Element chapterElement : chapterElements)
        {
            temp = chapterElement.select("span");
            String chapterUrl = mBaseUrl + temp.select("a").attr("href");
            String cTitle = temp.text().trim();
            String chapterDate = chapterElement.select("i").text();
            Chapter curChapter;
            curChapter = new Chapter(chapterUrl, title, cTitle, chapterDate);
            chapterList.add(curChapter);
        }


        //set chapter numbers
        int numChapters = chapterList.size() - 1;
        for (int i = 0; i <= numChapters; i++)
        {
            chapterList.get(i).setChapterNumber(numChapters);
            numChapters--;
        }
        return chapterList;
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
        final String[] baseChapterUrl = new String[1];
        return currentService.getResponse(request.getChapterUrl()).flatMap(response -> NetworkService.mapResponseToString(response)).flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml))).flatMap(url -> {
            baseChapterUrl[0] = url;
            return currentService.getResponse(url);
        }).flatMap(response1 -> currentService.mapResponseToString(response1)).flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml, baseChapterUrl[0]))).flatMap(imageUrls -> Observable.from(imageUrls.toArray(new String[imageUrls.size()]))).buffer(10).concatMap(batchedPageUrls -> {
            List<Observable<String>> imageUrlObservables = new ArrayList<>();
            for (String pageUrl : batchedPageUrls)
            {
                imageUrlObservables.add(Observable.just(pageUrl));
            }

            return Observable.zip(imageUrlObservables, args -> {
                List<String> imageUrls = new ArrayList<>();
                for (Object uncastImageUrl : args)
                {
                    imageUrls.add(String.valueOf(uncastImageUrl));
                }
                return imageUrls;
            });
        }).concatMap(batchedImageUrls -> Observable.from(batchedImageUrls.toArray(new String[batchedImageUrls.size()]))).doOnNext(imageUrl -> temporaryCachedImageUrls.add(imageUrl)).onBackpressureBuffer();

    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    public String parseHtmlToPageUrls(final String unparsedHtml)
    {
        //get base url for images
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        String imageUrl = parsedDocumentForImage.select("div.canvas").select("a.img-link").select("img").attr("src");

        //get img extension
        Pattern regex2 = Pattern.compile("(?!.*\\/).*");
        Matcher regexMatcher2 = regex2.matcher(imageUrl);
        regexMatcher2.find();
        String extension = regexMatcher2.group();

        String baseUrl = imageUrl.replace(extension, "");

        return baseUrl;
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @param url
     * @return
     */
    public List<String> parseHtmlToImageUrl(final String unparsedHtml, String url)
    {
        List<String> imageUrls = new ArrayList<>();
        String postfix;
        Document parsedDocumentForImage = Jsoup.parse(unparsedHtml);
        Elements imageUpdate = parsedDocumentForImage.select("a");

        int i = 0;
        for (Element e : imageUpdate)
        {
            if (i > 0)
            {
                postfix = e.select("a").attr("href");
                imageUrls.add(postfix);
            }
            i++;
        }

        //TODO FIX
        Collections.sort(imageUrls, (s1, s2) -> {
            String ss1 = s1.replaceAll("[^0-9]", "");
            String ss2 = s2.replaceAll("[^0-9]", "");
            return Integer.valueOf(ss1).compareTo(Integer.valueOf(ss2));
        });

        for (i = 0; i < imageUrls.size(); i++)
        {
            imageUrls.set(i, url + imageUrls.get(i));
        }

        return imageUrls;
    }

    /***
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @param aWrapper
     * @return
     */
    public Observable<Manga> updateMangaObservable(RequestWrapper aWrapper)
    {
        NetworkService curService = NetworkService.getTemporaryInstance();

        return curService.getResponse(aWrapper.getMangaUrl()).flatMap(response -> curService.mapResponseToString(response)).flatMap(html -> Observable.just(scrapeAndUpdateManga(html, aWrapper))).onErrorReturn(null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> throwable.printStackTrace());

    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @param aRequest
     * @return
     */
    public Manga scrapeAndUpdateManga(final String unparsedHtml, RequestWrapper aRequest)
    {
        Document html = Jsoup.parse(unparsedHtml);

        //summary
        String summary = html.body().select("p.summary").text();

        Elements e = html.body().select("table.outer");
        String img = e.select("div.cover").select("img").attr("href");
        if (img.equals(""))
        {
            img = e.select("div.cover").select("img").attr("src");
        }
        e = e.select("table.attr").select("tbody").select("tr");
        String alternate = null;
        String author = null;
        String artist = null;
        String genres = null;
        String status = null;
        Elements tList;
        for (int i = 0; i < e.size(); i++)
        {
            if (i == 3)
            {
                //alternative
                alternate = e.get(i).select("td").text();
            }
            else if (i == 4)
            {
                //author
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0)
                {
                    alternate = "";
                    for (Element auth : tList)
                    {
                        alternate += auth.text() + ",";
                    }
                }
                else
                {
                    alternate = "~";
                }
            }
            else if (i == 5)
            {
                //artist
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0)
                {
                    artist = "";
                    for (Element art : tList)
                    {
                        artist += art.text() + ",";
                    }
                }
                else
                {
                    artist = "~";
                }
            }
            else if (i == 6)
            {
                //genres
                tList = e.get(i).select("td").select("a");
                if (tList.size() > 0)
                {
                    genres = "";
                    for (Element gen : tList)
                    {
                        genres += gen.text() + ",";
                    }
                }
                else
                {
                    genres = "~";
                }
            }
            else if (i == 9)
            {
                //status
                status = e.get(i).select("td").text();
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

        MFDBHelper.getInstance().updateManga(values, aRequest.getMangaUrl());
        return MFDBHelper.getInstance().getManga(aRequest.getMangaUrl(), SourceKey);
    }
}