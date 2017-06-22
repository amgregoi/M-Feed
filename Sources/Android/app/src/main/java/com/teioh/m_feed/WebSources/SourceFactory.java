package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Utils.SharedPrefs;

public class SourceFactory
{
    /***
     * This function retrieves the current source.
     * @return
     */
    public SourceBase getSource()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource()).getSource();
    }

    /***
     * This function retrieves the current sources name.
     * @return
     */
    public String getSourceName()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource()).getSource().getCurrentSource().name();
    }


}
