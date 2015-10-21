package com.teioh.m_feed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class MangaFeedContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MangaFeedContract() {
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MangaFeedEntry.TABLE_NAME + " (" +
                    MangaFeedEntry._ID + " INTEGER PRIMARY KEY," +
                    MangaFeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    MangaFeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MangaFeedEntry.COLUMN__NAME_OBJECT + TEXT_TYPE + COMMA_SEP +
                    " )";
    //drops table if already exists
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MangaFeedEntry.TABLE_NAME;


    /* Inner class that defines the table contents */
    public static abstract class MangaFeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN__NAME_OBJECT = "obj";
    }

    public class MangaFeedDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public MangaFeedDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
