package com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers;

import com.teioh.m_feed.Models.Manga;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface MangaViewMapper {

    void setMangaViews(Manga manga);

    void setFollowButtonText(String newText);
}
