package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.WebSources.RequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

public class MangaHere extends SourceManga
{
    final public static String TAG = MangaHere.class.getSimpleName();

    final public static String SourceKey = "MangaHere";
    final private String mBaseUrl = "http://mangahere.co/";
    final private String mUpdatesUrl = "http://mangahere.co/latest/";
    final private String mGenres[] = {
            "Action",
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
            "Martial Arts",
            "Mature",
            "Mecha",
            "Mystery",
            "One Shot",
            "Psychological",
            "Romance",
            "School Life",
            "Sci-fi",
            "Seinen",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Sports",
            "Supernatural",
            "Tragedy",
            "Yaoi",
            "Yuri"
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public MangaEnums.eSourceType getSourceType()
    {
        return MangaEnums.eSourceType.MANGA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRecentUpdatesUrl()
    {
        return mUpdatesUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getGenres()
    {
        return mGenres;
    }

    /**
     * {@inheritDoc}
     */
    public List<Manga> parseResponseToRecentList(final String aResponseBody)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);
        Elements lUpdates = lParsedDocument.select("div.manga_updates");
        lParsedDocument = Jsoup.parse(lUpdates.toString());
        List<Manga> lMangaList = resolveMangaFromRecentDocument(lParsedDocument);

        return lMangaList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Manga parseResponseToManga(final RequestWrapper aRequest, final String aResponseBody)
    {
        Document lHtml = Jsoup.parse(aResponseBody);
        Elements lUsefulSection = lHtml.select("div.manga_detail_top.clearfix");

        //image url
        Element lImageElement = lUsefulSection.select("img").first();
        //summary
        Elements lHeaderInfo = lUsefulSection.select("ul.detail_topText").select("li");


        if (lImageElement != null && lHeaderInfo != null)
        {
            String lImage = lImageElement.attr("src");
            String lDescription = null;
            String lAlternate = null;
            String lAuthor = null;
            String lArtist = null;
            String lGenres = null;
            String lStatus = null;

            for (int i = 0; i < lHeaderInfo.size(); i++)
            {
                if (i == 2)
                {
                    lAlternate = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 4)
                {
                    lAuthor = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 5)
                {
                    lArtist = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 3)
                {
                    lGenres = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 6)
                {
                    lStatus = lHeaderInfo.get(i).text().replace(lHeaderInfo.get(i).select("label").text(), "");
                }
                else if (i == 8)
                {
                    lDescription = lHeaderInfo.get(i).text();
                }
            }

            Manga lNewManga = MangaDB.getInstance().getManga(aRequest.getMangaUrl());
            lNewManga.setAlternate(lAlternate);
            lNewManga.setPicUrl(lImage);
            lNewManga.setDescription(lDescription);
            lNewManga.setArtist(lArtist);
            lNewManga.setAuthor(lAuthor);
            lNewManga.setmGenre(lGenres);
            lNewManga.setStatus(lStatus);
            lNewManga.setSource(SourceKey);
            lNewManga.setMangaUrl(aRequest.getMangaUrl());


            MangaDB.getInstance().updateManga(lNewManga);
            MangaLogger.logInfo(TAG, "Finished creating/update manga (" + lNewManga.getTitle() + ")");
            return MangaDB.getInstance().getManga(aRequest.getMangaUrl());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);
        Elements lUpdates = lParsedDocument.select("div.detail_list").select("ul").not("ul.tab_comment.clearfix");
        lParsedDocument = Jsoup.parse(lUpdates.toString());
        List<Chapter> lChapterList = resolveChaptersFromParsedDocument(lParsedDocument, aRequest.getMangaTitle());

        return lChapterList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> parseResponseToPageUrls(final String aResponseBody)
    {
        List<String> lPageUrls = new ArrayList<>();

        //get base url for images
        Document lParsedDocumentForImage = Jsoup.parse(aResponseBody);
        Elements lImageUpdates = lParsedDocumentForImage.select("select.wid60").first().select("option");

        for (Element iUrl : lImageUpdates)
        {
            /***
             * MangaHere hotfix -> html changes to url links
             */
            String link = iUrl.attr("value");
            if (link.substring(0, 2).equals("//")) link = "http:" + link;
            link = link.replace(".cc", ".co");

            lPageUrls.add(link);
        }

        return lPageUrls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseResponseToImageUrls(final String aResponseBody, final String aResponseUrl)
    {
        Document lParsedDocumentForImage = Jsoup.parse(aResponseBody);
        String lUrl = lParsedDocumentForImage.select("section#viewer.read_img").select("img#image").attr("src");

        return lUrl;
    }

    /***
     * This helper function resolves chapters from the specified document.
     * Parent - parseResponseToChapters();
     *
     * @param aParsedDocument
     * @param aTitle
     * @return
     */
    private List<Chapter> resolveChaptersFromParsedDocument(final Document aParsedDocument, final String aTitle)
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

        MangaLogger.logInfo(TAG, " Finished parsing chapter list (" + aTitle + ")");
        return lChapterList;
    }

    /***
     * This helper function resolves manga objects from the specified document.
     * Parent - parseResponseToRecentList
     * @param aParsedDocument
     * @return
     */
    private List<Manga> resolveMangaFromRecentDocument(final Document aParsedDocument)
    {
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
                                                                             .logError(TAG, aThrowable.getMessage()))
                                                                     .subscribe();
                }
            }
        }

        MangaLogger.logInfo(TAG, " Finished parsing recent updates");

        if (lMangaList.size() == 0) return null;
        return lMangaList;
    }
}