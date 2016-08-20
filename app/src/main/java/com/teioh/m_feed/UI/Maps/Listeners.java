package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.Models.Manga;

public class Listeners {

    /**
     * Home screen fragments communicate with activity
     */
    public interface MainFragmentListener {
        boolean setRecentSelection(Long id);
        void updateRecentSelection(Manga manga);
    }

    /**
     * Chapter reading fragment communicates with ReaderActivity
     */
    public interface ReaderListener {
        void incrementChapter();

        void decrementChapter();

        void hideToolbar(long delay);

        void showToolbar();

        void updateToolbar(String mTitle, String cTitle, int size, int page);

        void updateCurrentPage(int position);

        void onBackPressed();
    }

    public interface MALDialogListener{
        void MALSignOut();
    }

}
