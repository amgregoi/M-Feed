package com.teioh.m_feed.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teioh.m_feed.Pojo.Manga;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MangaFeedDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MangaFeedContract.MangaFeedEntry.TABLE_NAME + " (" +
                    MangaFeedContract.MangaFeedEntry._ID + " INTEGER PRIMARY KEY, " +
                    MangaFeedContract.MangaFeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    MangaFeedContract.MangaFeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + " NOT NULL UNIQUE" + COMMA_SEP +
                    MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT + TEXT_TYPE + COMMA_SEP +
                    MangaFeedContract.MangaFeedEntry.COLUMN_NAME_DATETIME + DATE_TYPE + COMMA_SEP +
                    " )";
    //drops table if already exists
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MangaFeedContract.MangaFeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MangaFeed.db";

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

    //returns cursor containing listview contents if the db exists
    public Cursor dbExists() {
        //columns for return
        String[] projection = {
                MangaFeedContract.MangaFeedEntry._ID,
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_TITLE,
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT,};
        //sort order
        String sortOrder =
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_ENTRY_ID + " ASC";

        Cursor c = getReadableDatabase().query(
                MangaFeedContract.MangaFeedEntry.TABLE_NAME,  // The table to query
                projection,                 // The columns to return
                null,                       // The columns for the WHERE clause
                null,                       // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder                   // The sort order
        );
        if (c.getCount() > 0) return c;
        return null;
    }

    public void addMangaToTable(ArrayList<Manga> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Gson gson = new GsonBuilder().create();
        SQLiteDatabase sql = getWritableDatabase();
        for (Manga manga : list) {
            ContentValues values = new ContentValues();

            values.put(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_ENTRY_ID, manga.getMangaId());
            values.put(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_TITLE, manga.getTitle());
            values.put(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT, gson.toJson(manga));
            values.put(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_DATETIME, dateFormat.format(manga.getLastUpdated()));
            try {
                sql.insertOrThrow(
                        MangaFeedContract.MangaFeedEntry.TABLE_NAME,
                        MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT,
                        values);
            }catch(SQLiteConstraintException s){
                Log.i("SQLite", "Insesrt fail - not a unique manga (" + manga.getTitle() + ")");
            }
        }
        Log.e("DB_Finished", "database finished building");

        sql.close();
    }

    public Manga getMangaById(int id) {
        //columns for return
        Gson gson = new Gson();
        String[] projection = {
                MangaFeedContract.MangaFeedEntry._ID,
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_TITLE,
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT,};
        //sort order
        String sortOrder =
                MangaFeedContract.MangaFeedEntry.COLUMN_NAME_ENTRY_ID + " ASC";

        String whereClause = MangaFeedContract.MangaFeedEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] whereValues = {Integer.toString(id)};
        Cursor c = getReadableDatabase().query(
                MangaFeedContract.MangaFeedEntry.TABLE_NAME,  // The table to query
                projection,                 // The columns to return
                whereClause,                       // The columns for the WHERE clause
                whereValues,                       // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder                   // The sort order
        );
        if (c.getCount() > 0) {
            c.moveToFirst();
            String obj = c.getString(c.getColumnIndex(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT));
            Manga manga = gson.fromJson(obj, Manga.class);
            return manga;
        }
        return null;
    }

    public void updateMangaFollow(Manga manga) {
        Gson gson = new Gson();
        ContentValues data = new ContentValues();
        data.put(MangaFeedContract.MangaFeedEntry.COLUMN_NAME_OBJECT, gson.toJson(manga));
        getWritableDatabase().update(MangaFeedContract.MangaFeedEntry.TABLE_NAME, data, "_id=" + manga.getDBID(), null);
    }

    //TODO
    public void syncDatabase() {

    }
}