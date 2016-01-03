package com.teioh.m_feed.UI.MangaActivity.Presenters;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface MangaInformationPresenter {

    void initialize();

    void getMangaViewInfo();

    void onFollwButtonClick();

    void setFollowButtonText(boolean follow, boolean notInit);

    void onResume();

    void onPause();

    void onDestroyView();

}
