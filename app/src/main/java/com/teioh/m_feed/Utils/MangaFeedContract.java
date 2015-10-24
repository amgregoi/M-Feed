package com.teioh.m_feed.Utils;

import android.provider.BaseColumns;

public final class MangaFeedContract {

    public MangaFeedContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class MangaFeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Manga";
        public static final String COLUMN_NAME_ENTRY_ID = "mangaId";
        public static final String COLUMN_NAME_TITLE = "mangaTitle";
        public static final String COLUMN__NAME_OBJECT = "mangaObj";
    }


}

