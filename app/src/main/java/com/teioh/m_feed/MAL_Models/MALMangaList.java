package com.teioh.m_feed.MAL_Models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "Manga")
public class MALMangaList {
    @ElementList(entry="entry", inline=true)
    public ArrayList<MALManga> mMangaList;
}
