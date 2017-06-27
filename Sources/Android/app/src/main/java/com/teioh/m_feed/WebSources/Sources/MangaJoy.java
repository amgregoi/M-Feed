package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceBase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

public class MangaJoy extends SourceManga
{
    final public static String TAG = MangaJoy.class.getSimpleName();

    final private String SourceKey = "MangaJoy";
    final private String mBaseUrl = "http://funmanga.com/";
    final private String mUpdatesUrl = "http://funmanga.com/latest-chapters";
    final private String mGenres[] = {"Joy",
            "Action",
            "Adult",
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
            "Lolicon",
            "Manga",
            "Manhua",
            "Manhwa",
            "Martial Arts",
            "Mature",
            "Mecha",
            "Mystery",
            "One shot",
            "Psychological",
            "Romance",
            "School Life",
            "Sci fi",
            "Seinen",
            "Shotacon",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Smut",
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
    @Override
    public List<Manga> parseResponseToRecentList(final String aResponseBody)
    {
        List<Manga> lMangaList = new ArrayList<>();

        try
        {
            Document lParsedDocument = Jsoup.parse(aResponseBody);
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
                                                                                 .logError(TAG, aThrowable.getMessage()))
                                                                         .onErrorReturn(null)
                                                                         .subscribe();
                    }
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, " Failed to parse recent updates: ");
        }

        MangaLogger.logInfo(TAG, "Finished parsing recent updates");

        if (lMangaList.size() == 0) return null;
        return lMangaList;


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Manga parseResponseToManga(final RequestWrapper aRequest, final String aResponseBody)
    {
        Document lHtml = Jsoup.parse(aResponseBody);

        try
        {
            Element lImageElement = lHtml.body().select("img.img-responsive.mobile-img").first();
            Element lDescriptionElement = lHtml.body().select("div.note.note-default.margin-top-15").first();
            Elements lInfo = lHtml.body().select("dl.dl-horizontal").select("dd");

            String lImage = lImageElement.attr("src");
            String lDescription = lDescriptionElement.text();
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

            Manga lManga = MangaDB.getInstance().getManga(aRequest.getMangaUrl());
            lManga.setAlternate(lAlternate);
            lManga.setPicUrl(lImage);
            lManga.setDescription(lDescription);
            lManga.setArtist(lArtist);
            lManga.setAuthor(lAuthor);
            lManga.setmGenre(lGenres);
            lManga.setStatus(lStatus);
            lManga.setSource(SourceKey);
            lManga.setMangaUrl(aRequest.getMangaUrl());

            MangaDB.getInstance().updateManga(lManga);

            MangaLogger.logInfo(TAG, "Finished creating/updating manga (" + lManga.getTitle() + ")");
            return MangaDB.getInstance().getManga(aRequest.getMangaUrl());
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);
        List<Chapter> lChapterList = resolveChaptersFromParsedDocument(lParsedDocument, aRequest.getMangaTitle());

        return lChapterList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> parseResponseToPageUrls(final String aResponseBody)
    {
        List<String> lImages = new ArrayList<>();

        Document lDoc = Jsoup.parse(aResponseBody);
        Elements lNav = lDoc.select("h5.widget-heading").select("select").select("option");

        int lPages = lNav.size();

        for (int i = 1; i < lPages; i++)
        {
            String lLink = lNav.get(i).attr("value");
            lImages.add(lLink);
        }

        return lImages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseResponseToImageUrls(final String aResponseBody, final String aResponseUrl)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);
        String lLink = lParsedDocument.select("img.img-responsive").attr("src");

        return lLink;
    }

    /***
     * This helper function resolves chapters from the specified document and returns a list of chapters.
     * Parent - parseResponseToChapters();
     *
     * @param aParsedDocument
     * @param aTitle
     * @return
     */
    private List<Chapter> resolveChaptersFromParsedDocument(final Document aParsedDocument, final String aTitle)
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
}



