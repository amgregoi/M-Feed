package com.teioh.m_feed.UI.ReaderActivity;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

import java.util.ArrayList;
import java.util.List;

public class CurrentSelection
{
    private static Manga lCurrentManga;
    private static List<Chapter> lCurrentChapters;


    public static void setChapters(List<Chapter> aChapters)
    {
        lCurrentChapters = new ArrayList<>(aChapters);
    }

    public static void setManga(Manga aManga)
    {
        lCurrentManga = new Manga(aManga);
    }

    public static List<Chapter> getChapters()
    {
        return lCurrentChapters;
    }

    public static Manga getManga()
    {
        return lCurrentManga;
    }


}
