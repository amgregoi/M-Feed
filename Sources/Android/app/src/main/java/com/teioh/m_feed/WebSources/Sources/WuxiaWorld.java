package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceBase;

import java.util.ArrayList;
import java.util.List;

import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by amgregoi on 6/22/17.
 */
public class WuxiaWorld extends SourceBase
{
    final private String SourceKey = "MangaJoy";

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
        return "http://www.google.com";
    }

    @Override
    public String[] getGenres()
    {
        return new String[0];
    }

    @Override
    public List<Manga> parseResponseToRecentList(String aResponseBody)
    {
        //Testing stuff out
        List<Manga> lTemp = new ArrayList<>();
        Manga lManga = new Manga("test", getRecentUpdatesUrl(), SourceKey);
        lManga.set_id(new Long(100000));
        lManga.setInitialized(1);
        lTemp.add(lManga);
        return lTemp;
    }

    @Override
    public Manga parseResponseToManga(RequestWrapper aRequest, String aResponseBody)
    {
        return null;
    }

    @Override
    public List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody)
    {
        List<Chapter> lTemp = new ArrayList<>();
        Chapter lChapter = new Chapter("this is the sound of the police");
        Chapter lChapter2 = new Chapter("you better run");
        lChapter.setChapterUrl(getRecentUpdatesUrl());
        lChapter2.setChapterUrl(getRecentUpdatesUrl());
        lTemp.add(lChapter);
        lTemp.add(lChapter2);
        return lTemp;
    }

    @Override
    public List<String> parseResponseToPageUrls(String aResponseBody)
    {
        return null;
    }

    @Override
    public String parseResponseToImageUrls(String aResponseBody, String aResponseUrl)
    {
        throw new OnErrorNotImplementedException(new Throwable("Not Implemented"));
    }
}
