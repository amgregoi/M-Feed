package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.Utils.SharedPrefs;

public class SourceFactory {

    public Source getSource(){
        return SourceType.valueOf(SharedPrefs.getSavedSource()).getSource();
    }

    public String getSourceName(){
        return SourceType.valueOf(SharedPrefs.getSavedSource()).getSource().getSourceType().name();
    }
}
