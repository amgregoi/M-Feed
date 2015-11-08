package com.teioh.m_feed.Utils.OttoBus;

import com.teioh.m_feed.Models.Manga;

/**
 * Created by Asus1 on 10/21/2015.
 */
public class RemoveFromLibrary {
    Manga manga;

    public RemoveFromLibrary(Manga m) {
        manga = m;
    }

    public Manga getManga() {
        return manga;
    }
}
