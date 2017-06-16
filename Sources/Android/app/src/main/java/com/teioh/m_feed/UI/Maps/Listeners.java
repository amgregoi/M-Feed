package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.Models.Manga;

public interface Listeners
{

    interface DialogYesNoListener
    {
        void positive(int aAction);
        void negative(int aAction);
    }

    /**
     * Home screen fragments communicate with activity
     */
    interface MainFragmentListener
    {

        boolean setRecentSelection(Long aId);

        boolean updateRecentSelection(Manga aManga);

        boolean removeFilters();
    }

    /**
     * Chapter reading fragment communicates with ReaderActivity
     */
    interface ReaderListener
    {
        void incrementChapter();

        void decrementChapter();

        void hideToolbar(long aDelay);

        void showToolbar();

        void updateToolbar(String aTitle, String aChapterTitle, int aSize, int aPage);

        void updateCurrentPage(int aPosition);

        void onBackPressed();

        boolean checkActiveChapter(int aChapter);
    }

}
