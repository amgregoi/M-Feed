package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.Models.Manga;

public interface Listeners {

    /**
     * Home screen fragments communicate with activity
     */
    interface MainFragmentListener {

        boolean setRecentSelection(Long aId);

        void updateRecentSelection(Manga aManga);

        void removeFilters();
    }

    /**
     * Chapter reading fragment communicates with ReaderActivity
     */
    public interface ReaderListener {
        void incrementChapter();

        void decrementChapter();

        void hideToolbar(long aDelay);

        void showToolbar();

        void updateToolbar(String aTitle, String aChapterTitle, int aSize, int aPage);

        void updateCurrentPage(int aPosition);

        void onBackPressed();

        boolean checkActiveChapter(int aChapter);
    }

    public interface MALDialogListener{
        void MALSignOut();
    }

}
