package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.schedulers.Schedulers;

public class MangaEden extends SourceBase
{
    final public static String TAG = MangaEden.class.getSimpleName();

    final public String SourceKey = "MangaEden";

    final String mBaseUrl = "http://www.mangaeden.com";
    final String mUpdatesUrl = "http://www.mangaeden.com/ajax/news/1/0/";

    @Override
    public String getRecentUpdatesUrl()
    {
        return mUpdatesUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Manga> parseResponseToRecentList(final String aResponseBody)
    {
        List<Manga> lMangaList = new ArrayList<>();
        Elements lMangaElements = Jsoup.parse(aResponseBody).select("body > li");

        for (Element iMangaBlock : lMangaElements)
        {
            Element iUrlElement = iMangaBlock.select("div.newsManga").first();
            Element iTitleElement = iMangaBlock.select("div.manga_tooltop_header > a").first();

            String lTitle = iTitleElement.text();
            String lUrl = "https://www.mangaeden.com/api/manga/" + iUrlElement.id().substring(0, 24) + "/";

            Manga lManga = MangaDB.getInstance().getManga(lUrl);

            if (lManga != null)
            {
                lMangaList.add(lManga);
            }
            else
            {
                lManga = new Manga(lTitle, lUrl, SourceKey);
                lMangaList.add(lManga);
                MangaDB.getInstance().putManga(lManga);
                updateMangaObservable(new RequestWrapper(lManga)).subscribeOn(Schedulers.computation())
                                                                 .doOnError(aThrowable -> MangaLogger
                                                                         .logError(TAG, aThrowable.getMessage()))
                                                                 .subscribe();
            }
        }

        MangaLogger.logInfo(TAG, "Finished parsing recent updates");

        if (lMangaList.size() == 0) return null;
        return lMangaList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        List<Chapter> lChapterList = null;

        try
        {
            JSONObject lParsedJsonObject = new JSONObject(aResponseBody);

            lChapterList = resolveChaptersFromParsedJson(lParsedJsonObject);
            lChapterList = setNumberForChapterList(lChapterList);
        }
        catch (JSONException aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        MangaLogger.logInfo(TAG, "Finished parsing chapter list (" + aRequest.getMangaTitle() + ")");
        return lChapterList;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> parseResponseToPageUrls(final String aResponseBody)
    {
        List<String> lImageList = null;

        try
        {
            JSONObject lParsedJson = new JSONObject(aResponseBody);
            lImageList = new ArrayList<>();

            JSONArray lImageArrayNodes = lParsedJson.getJSONArray("images");
            for (int i = 0; i < lImageArrayNodes.length(); i++)
            {
                JSONArray lCurrentImageNode = lImageArrayNodes.getJSONArray(i);

                lImageList.add("https://cdn.mangaeden.com/mangasimg/" + lCurrentImageNode.getString(1));
            }

            Collections.reverse(lImageList);
        }
        catch (JSONException aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        MangaLogger.logInfo(TAG, "Finished parsing page url list.");
        return lImageList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseResponseToImageUrls(String aResponseBody, final String aResponseUrl)
    {
        //Note: This method was added to keep consistency with the other sources
        return aResponseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Manga parseResponseToManga(final RequestWrapper aRequest, final String aResponseBody)
    {
        try
        {
            JSONObject lParsedJsonObject = new JSONObject(aResponseBody);

            String lGenres = "";
            JSONArray lGenreArrayNodes = lParsedJsonObject.getJSONArray("categories");
            for (int i = 0; i < lGenreArrayNodes.length(); i++)
            {
                if (i != lGenreArrayNodes.length() - 1)
                {
                    lGenres += lGenreArrayNodes.getString(i) + ", ";
                }
                else
                {
                    lGenres += lGenreArrayNodes.getString(i);
                }
            }

            Manga lNewManga = MangaDB.getInstance().getManga(aRequest.getMangaUrl());

            lNewManga.setArtist(lParsedJsonObject.getString("artist"));
            lNewManga.setAuthor(lParsedJsonObject.getString("author"));
            lNewManga.setDescription(lParsedJsonObject.getString("description").trim());
            lNewManga.setmGenre(lGenres);
            lNewManga.setPicUrl("https://cdn.mangaeden.com/mangasimg/" + lParsedJsonObject.getString("image"));
            lNewManga.setInitialized(1);

            MangaDB.getInstance().updateManga(lNewManga);
            MangaLogger.logError(TAG, "Finished creating/update manga (" + lNewManga.getTitle() + ")");
            return lNewManga;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            return null;
        }
    }

    /***
     * This helper function resolves and builds chapters from the parsed json
     * Parent - parseResponseToChapters();
     *
     * @param aParsedJson
     * @return
     * @throws JSONException
     */
    private List<Chapter> resolveChaptersFromParsedJson(JSONObject aParsedJson) throws JSONException
    {
        List<Chapter> lChapterList = new ArrayList<>();

        String lMangaTitle = aParsedJson.getString("title");
        JSONArray lChapterArrayNodes = aParsedJson.getJSONArray("chapters");
        for (int i = 0; i < lChapterArrayNodes.length(); i++)
        {
            JSONArray lCurrentChapterArray = lChapterArrayNodes.getJSONArray(i);

            Chapter lCurrentChapter = constructChapterFromJSONArray(lCurrentChapterArray, lMangaTitle);

            lChapterList.add(lCurrentChapter);
        }

        Collections.reverse(lChapterList);
        return lChapterList;
    }

    /***
     * This helper function constructs a chapter from the specified JSON.
     * Parent - resolveChaptersFromParsedJson();
     *
     * @param aChapterNode
     * @param aMangaName
     * @return
     * @throws JSONException
     */
    private Chapter constructChapterFromJSONArray(JSONArray aChapterNode, String aMangaName) throws JSONException
    {
        Chapter lNewChapter = new Chapter(aMangaName);

        lNewChapter.setChapterUrl("https://www.mangaeden.com/api/chapter/" + aChapterNode.getString(3) + "/");
        lNewChapter.setChapterTitle(aMangaName + " " + aChapterNode.getDouble(0));

        Date lDate = new Date(aChapterNode.getLong(1) * 1000);
        lNewChapter.setChapterDate(lDate.toString());

        return lNewChapter;
    }

    /***
     * This helper function sets the chapters index value.
     * Parent - parseResponseToChapters();
     *
     * @param aChapterList
     * @return
     */
    private List<Chapter> setNumberForChapterList(List<Chapter> aChapterList)
    {
        Collections.reverse(aChapterList);
        for (int i = 0; i < aChapterList.size(); i++)
        {
            aChapterList.get(i).setChapterNumber(i + 1);
        }

        return aChapterList;
    }
}