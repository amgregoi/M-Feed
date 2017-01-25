package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Utils.SharedPrefs;

public class SourceFactory
{
    /***
     * TODO..
     * @return
     */
    public SourceBase getSource()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource()).getSource();
    }

    /***
     * TODO..
     * @return
     */
    public String getSourceName()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource()).getSource().getSourceType().name();
    }
}
