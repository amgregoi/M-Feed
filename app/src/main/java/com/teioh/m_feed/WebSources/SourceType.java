package com.teioh.m_feed.WebSources;

import com.teioh.m_feed.WebSources.Sources.Batoto;
import com.teioh.m_feed.WebSources.Sources.MangaEden;
import com.teioh.m_feed.WebSources.Sources.MangaHere;
import com.teioh.m_feed.WebSources.Sources.MangaJoy;
import com.teioh.m_feed.WebSources.Sources.MangaPark;

/***
 * TODO...
 *
 */
public enum SourceType {
    Batoto(new Batoto()),
    MangaEden(new MangaEden()),
    MangaHere(new MangaHere()),
    MangaJoy(new MangaJoy()),
    MangaPark(new MangaPark());

    Source lSource;

    SourceType(Source aSource){
        lSource = aSource;
    }

    Source getSource(){
        return lSource;
    }
}
