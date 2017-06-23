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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.schedulers.Schedulers;

public class Batoto extends SourceManga
{
    final public static String TAG = Batoto.class.getSimpleName();

    final public String SourceKey = "Batoto";
    final private String mBaseUrl = "http://bato.to/";
    final private String mUpdatesUrl = "http://bato.to/search_ajax?order_cond=update&order=desc&p=";
    final private String mGenres[] = {
            "4-Koma",
            "Action",
            "Adventure",
            "Award Winning",
            "Comedy",
            "Cooking",
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
            "Mecha",
            "Medical",
            "Music",
            "Mystery",
            "Oneshot",
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
            "Smut", "Sports",
            "Supernatural", "Tragedy", "Webtoon", "Yaoi", "Yuri"
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
    public List<Manga> parseResponseToRecentList(String aResponseBody)
    {
        List<Manga> lMangaList = new ArrayList<>();

        if (!aResponseBody.contains("No (more) comics found!"))
        {
            Document lParseDocuments = Jsoup.parse(aResponseBody);
            Elements lMangaElements = lParseDocuments.select("tr:not([id]):not([class])");


            for (Element iHtmlBlock : lMangaElements)
            {
                Element lUrlElement = iHtmlBlock.select("a[href^=http://bato.to]").first();
                Element lNameElement = lUrlElement;

                String lMangaUrl = lUrlElement.attr("href");
                String lMangaTitle = lNameElement.text().trim();
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

        MangaLogger.logInfo(TAG, "Finished parsing recent updates");

        return lMangaList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Manga parseResponseToManga(final RequestWrapper aRequest, final String aResponseBody)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);

        Element lArtistElement = lParsedDocument.select("a[href^=http://bato.to/search?artist_name]").first();
        Element lDescriptionElement = lParsedDocument.select("tr").get(6);
        Elements lGenreElements = lParsedDocument.select("img[src=http://bato.to/forums/public/style_images/master/bullet_black.png]");
        Element lThumbnailElement = lParsedDocument.select("img[src^=http://img.bato.to/forums/uploads/]").first();

        Manga lNewManga = MangaDB.getInstance().getManga(aRequest.getMangaUrl());

        if (lNewManga == null) lNewManga = new Manga(aRequest.getMangaTitle(), aRequest.getMangaUrl(), SourceKey);

        if (lArtistElement != null)
        {
            String lArtist = lArtistElement.text();
            lNewManga.setArtist(lArtist);
            lNewManga.setAuthor(lArtist);
        }

        if (lDescriptionElement != null)
        {
            String lDescription = lDescriptionElement.text().substring("Description:".length()).trim();
            lNewManga.setDescription(lDescription);
        }

        if (lGenreElements != null)
        {
            String lGenres = "";
            for (int i = 0; i < lGenreElements.size(); i++)
            {
                String lCurrentGenre = lGenreElements.get(i).attr("alt");

                if (i < lGenreElements.size() - 1)
                {
                    lGenres += lCurrentGenre + ", ";
                }
                else
                {
                    lGenres += lCurrentGenre;
                }
            }

            lNewManga.setmGenre(lGenres);
        }

        if (lThumbnailElement != null)
        {
            String lThumbnail = lThumbnailElement.attr("src");
            lNewManga.setPicUrl(lThumbnail);
        }

        boolean lStatus = aResponseBody.contains("<td>Complete</td>");
        if (lStatus) lNewManga.setStatus("Complete");
        else lNewManga.setStatus("Ongoing");

        lNewManga.setInitialized(1);

        MangaDB.getInstance().putManga(lNewManga);
        return lNewManga;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        List<Chapter> lChapterList = new ArrayList<>();

        Document lParsedDocument = Jsoup.parse(aResponseBody);
        Elements lChapterElements = lParsedDocument.select("tr.row.lang_English.chapter_row");
        for (Element iChapterElement : lChapterElements)
        {

            Chapter lNewChapter = new Chapter();

            Element lUrlElement = iChapterElement.select("a").first();
            Element lDateElement = iChapterElement.select("td").get(4);

            if (lUrlElement != null)
            {
                String lUrl = lUrlElement.attr("href");
                lNewChapter.setChapterUrl(lUrl);

                String lTitle = lUrlElement.text().trim();
                lNewChapter.setChapterTitle(lTitle);
            }
            if (lDateElement != null)
            {
                try
                {
                    long lDate = new SimpleDateFormat("dd MMMMM yyyy - hh:mm a", Locale.ENGLISH).parse(lDateElement.text()).getTime();
                    lNewChapter.setChapterDate(new Date(lDate).toString());
                }
                catch (ParseException e)
                {
                    MangaLogger.logError(TAG, e.getMessage());
                }
            }

            lNewChapter.setMangaTitle(aRequest.getMangaTitle());
            lChapterList.add(lNewChapter);
        }

        Collections.reverse(lChapterList);
        for (int i = 0; i < lChapterList.size(); i++)
        {
            lChapterList.get(i).setChapterNumber(i + 1);
        }

        return lChapterList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> parseResponseToPageUrls(final String aResponseBody)
    {
        Document lParsedDocument = Jsoup.parse(aResponseBody);

        List<String> lPageList = new ArrayList<>();

        Elements lPageElements = lParsedDocument.getElementById("page_select").getElementsByTag("option");
        for (Element iPageElement : lPageElements)
        {
            lPageList.add(iPageElement.attr("value"));
        }

        return lPageList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseResponseToImageUrls(final String aResponseBody, final String aResponseUrl)
    {
        int lStart = aResponseBody.indexOf("<img id=\"comic_page\"");
        int lEnd = aResponseBody.indexOf("</a>", lStart);
        String lTrimmedHtml = aResponseBody.substring(lStart, lEnd);

        Document lParsedDocument = Jsoup.parse(lTrimmedHtml);
        Element lImageElement = lParsedDocument.getElementById("comic_page");

        return lImageElement.attr("src");
    }

}