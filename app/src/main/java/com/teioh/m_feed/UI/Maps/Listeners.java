package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.Models.Manga;

public class Listeners {

    /**
     * Home screen fragments communicate with activity
     */
    public interface MainFragmentListener {
        boolean setRecentSelection(Long aId);
        void updateRecentSelection(Manga aManga);
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
    }

    public interface MALDialogListener{
        void MALSignOut();
    }

}
