package com.teioh.m_feed.WebSources.Sources;

import android.util.Log;

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
import java.util.Collections;
import java.util.List;

/**
 * Created by amgregoi on 6/22/17.
 */
public class WuxiaWorld extends SourceNovel
{
    private final static String TAG = WuxiaWorld.class.getSimpleName();

    private final String SourceKey = "MangaJoy";

    private String mChineseId = "li#menu-item-2165";
    private String mKoreanId = "li#menu-item-116520";

    /**
     * {@inheritDoc}
     */
    @Override
    public MangaEnums.eSourceType getSourceType()
    {
        return MangaEnums.eSourceType.NOVEL;
    }

    @Override
    public String getRecentUpdatesUrl()
    {
        return "http://www.wuxiaworld.com/";
    }

    @Override
    public String[] getGenres()
    {
        return new String[0];
    }

    @Override
    public List<Manga> parseResponseToRecentList(String aResponseBody)
    {

        List<Manga> lTemp = new ArrayList<>();

        try
        {
            Document lParsedDocument = Jsoup.parse(aResponseBody);
            Elements lChineseElements = lParsedDocument.select("ul#menu-home-menu.menu").select(mChineseId).select("ul.sub-menu");
            Elements lKoreanElements = lParsedDocument.select("ul#menu-home-menu.menu").select(mKoreanId).select("ul.sub-menu");


            for (Element lChineseElement : lChineseElements)
            {
                Document lMenuItems = Jsoup.parse(lChineseElement.toString());
                Elements lContentElements = lMenuItems.select("a");

                Manga lManga;
                String lUrl, lTitle;

                for (Element iContent : lContentElements)
                {
                    lUrl = iContent.attr("href");
                    lTitle = iContent.text();

                    lManga = MangaDB.getInstance().getManga(lUrl);
                    if (lManga == null)
                    {
                        lManga = new Manga(lTitle, lUrl, SourceKey);
                        MangaDB.getInstance().putManga(lManga);
                    }
                    lTemp.add(lManga);
                }
            }
            for (Element lKoreanElement : lKoreanElements)
            {
                Document lMenuItems = Jsoup.parse(lKoreanElement.toString());
                Elements lContentElements = lMenuItems.select("a");

                Manga lManga;
                String lUrl, lTitle;

                for (Element iContent : lContentElements)
                {
                    lUrl = iContent.attr("href");
                    lTitle = iContent.text();

                    lManga = MangaDB.getInstance().getManga(lUrl);
                    if (lManga == null)
                    {
                        lManga = new Manga(lTitle, lUrl, SourceKey);
                        MangaDB.getInstance().putManga(lManga);
                    }
                    lTemp.add(lManga);
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, " Failed to parse recent updates: ");
        }


        //Testing stuff out
        Manga lManga = new Manga("test", getRecentUpdatesUrl(), SourceKey);
        lTemp.add(lManga);
        return lTemp;
    }

    @Override
    public Manga parseResponseToManga(RequestWrapper aRequest, String aResponseBody)
    {
        try
        {
            Document lParsedDocument = Jsoup.parse(aResponseBody);
            Elements lContent = lParsedDocument.select("div.entry-content").select("div").get(2).getAllElements().select("p");
            Element lImage = lContent.select("img").first();

            boolean lSynopsis = false;
            String lSynopsisText = "", lImageUrl = "";

            if (lImage != null)
                lImageUrl = lImage.attr("src");

            for (int i = 0; i < lContent.size(); i++)
            {
                Element lElement = lContent.get(i);


                Elements lText = null;
                if (lElement.children().size() > 0)
                {
                    lText = lElement.child(0).getAllElements();
                }

                if (lText != null)
                {
                    String lStrongText = lText.text();
                    if (lStrongText.contains("Synopsis"))
                    {
                        lSynopsis = true;
                    }
                }
                else
                {
                    if (lSynopsis)
                        lSynopsisText += lElement.text();
                }

            }

            Manga lManga = MangaDB.getInstance().getManga(aRequest.getMangaUrl());
            lManga.setAuthor("N/A");
            lManga.setArtist("N/A");
            lManga.setDescription(lSynopsisText);
            lManga.setmGenre("N/A");
            lManga.setStatus("N/A");
            lManga.setInitialized(1);
            lManga.setPicUrl(lImageUrl);
            MangaDB.getInstance().updateManga(lManga);

            return lManga;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, " Failed to update manga: ");
        }

        return null;
    }

    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        List<Chapter> lChapterList = new ArrayList<>();

        try
        {
            Document lParsedDocument = Jsoup.parse(aResponseBody);
            Elements lContent = lParsedDocument.select("div.entry-content").select("div").get(2).getAllElements().select("a");

            int lCount = 1;

            for (Element iChapter : lContent)
            {
                Log.e(TAG, iChapter.text());

                String lChapterName = iChapter.text().toLowerCase();
                if (lChapterName.contains("chapter"))
                {
                    String lUrl = iChapter.attr("href");
                    String lChapterTitle = iChapter.text();
                    lChapterList.add(new Chapter(lUrl, aRequest.getMangaTitle(), lChapterTitle, "-", lCount));
                    lCount++;
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        Collections.reverse(lChapterList); //Descending order
        return lChapterList;
    }

    @Override
    public List<String> parseResponseToPageUrls(String aResponseBody)
    {
        return null;
    }

    @Override
    public String parseResponseToImageUrls(String aResponseBody, String aResponseUrl)
    {

        String lText = "";

        try
        {
            Document lParsedDocument = Jsoup.parse(aResponseBody);
            Elements lContent = lParsedDocument.select("div.entry-content").select("div").get(2).getAllElements().select("p");

            String lFilter = "Next Chapter";
            String lFilter2 = "Previous Chapter";
            for (Element iElement : lContent)
            {
                if (!iElement.text().contains(lFilter) && !iElement.text().contains(lFilter2))
                    lText += iElement.text() + "\n\n";
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        return lText;
    }
}
