package com.teioh.m_feed.Utils.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Manga;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaFeedDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DB_PATH = "/data/data/com.teioh.m_feed/databases/";
    private static final String DB_NAME = "MangaFeed.db";
    private static MangaFeedDbHelper aInstance;
    private Context myContext;

    public MangaFeedDbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    public static synchronized MangaFeedDbHelper getInstance() {
        if (aInstance == null) {
            aInstance = new MangaFeedDbHelper(MFeedApplication.getInstance());
        }
        return aInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

    public void createDatabase() {
        createDB();
    }

    private void createDB() {
        boolean dbExist = DBExists();
        if (!dbExist) {
            this.getReadableDatabase();
            copyDBFromResource();
        }
    }

    private boolean DBExists() {
        SQLiteDatabase db = null;

        try {
            File database = myContext.getDatabasePath(DB_NAME);
            if (database.exists()) {
                db = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                db.setLocale(Locale.getDefault());
                db.setVersion(1);
            }
        } catch (Exception e) {
            Log.e("SqlHelper", "database not found");
        }

        if (db != null) {
            db.close();
        }
        return db != null;
    }

    private void copyDBFromResource() {
        String dbFilePath = DB_PATH + DB_NAME;
        try {
            InputStream inputStream = MFeedApplication.getInstance().getAssets().open(DB_NAME);
            OutputStream outStream = new FileOutputStream(dbFilePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            outStream.flush();
            outStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new Error("Problem copying database from resource file.");
        }
    }

    public void updateMangaFollow(String title) {
        ContentValues values = new ContentValues(1);
        values.put("following", 1);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, values, "title = ?", title);
    }

    public void updateMangaUnfollow(String title) {
        ContentValues values = new ContentValues(1);
        values.put("following", 0);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, values, "title = ?", title);
    }
}