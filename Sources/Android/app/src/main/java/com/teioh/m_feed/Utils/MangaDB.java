package com.teioh.m_feed.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.Subscriber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaDB extends SQLiteOpenHelper
{
    public static final String TAG = MangaDB.class.getSimpleName();

    public static final int sDATABASE_VERSION = 1;
    private static final String sDB_PATH = "/data/data/com.teioh.m_feed/databases/";
    private static final String sDB_NAME = "MangaFeed.db";
    private static MangaDB sInstance;
    private Context mContext;

    public MangaDB(Context aContext)
    {
        super(aContext, sDB_NAME, null, sDATABASE_VERSION);
        mContext = aContext;
    }

    /***
     * This function gets an instance of the MangaDB helper.
     */
    public static synchronized MangaDB getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new MangaDB(MFeedApplication.getInstance());
        }
        return sInstance;
    }

    /***
     * This function creates the database tables.
     */
    public void onCreate(SQLiteDatabase aDb)
    {
        cupboard().withDatabase(aDb).createTables();
    }

    /***
     * This function upgrades the databse.
     */
    public void onUpgrade(SQLiteDatabase aDb, int aOldVersion, int aNewVersion)
    {
        cupboard().withDatabase(aDb).upgradeTables();
    }

    /***
     * This function creates the database.
     */
    public void createDatabase()
    {
        createDB();
    }

    /***
     * This function verifies if the app needs to copy the shipped database.
     */
    private void createDB()
    {
        boolean dbExist = DBExists();
        if (!dbExist)
        {
            this.getReadableDatabase();
            copyDBFromResource();
        }
    }

    /***
     * This function verifies if the database has been copied already.
     *
     * @return true if database exists
     */
    private boolean DBExists()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        SQLiteDatabase db = null;

        try
        {
            File database = mContext.getDatabasePath(sDB_NAME);
            if (database.exists())
            {
                db = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                db.setLocale(Locale.getDefault());
                db.setVersion(1);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage(), "Database not found");
        }

        if (db != null)
        {
            db.close();
        }
        return db != null;
    }

    /***
     * This function copies the database provided with the apk.
     */
    private void copyDBFromResource()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        String dbFilePath = sDB_PATH + sDB_NAME;
        try
        {
            InputStream inputStream = MFeedApplication.getInstance().getAssets().open(sDB_NAME);
            OutputStream outStream = new FileOutputStream(dbFilePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
            {
                outStream.write(buffer, 0, length);
            }

            outStream.flush();
            outStream.close();
            inputStream.close();
        }
        catch (IOException aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function updates the follow status of an item in the database
     *
     * @param aTitle
     * @param aValue
     */
    public void updateMangaFollow(String aTitle, int aValue)
    {
        ContentValues values = new ContentValues(1);
        values.put("following", aValue);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, values, "title = ?", aTitle);
    }

    /***
     * This function updates the follow status of an item in the database (unfollow)
     *
     * @param aTitle
     */
    public void updateMangaUnfollow(String aTitle)
    {
        ContentValues values = new ContentValues(1);
        values.put("following", 0);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, values, "title = ?", aTitle);
    }

    /**
     * This function retrieves the list of followed items from the database.
     *
     * @return Observable arraylist of users followed manga
     */
    public Observable<ArrayList<Manga>> getLibraryList()
    {
        return Observable.create(subscriber ->
                                 {
                                     try
                                     {
                                         ArrayList<Manga> mangaList = new ArrayList<>();
                                         QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase())
                                                                                    .query(Manga.class)
                                                                                    .withSelection("NOT following = ? AND source = ?", "0", new SourceFactory()
                                                                                            .getSourceName())
                                                                                    .query();

                                         for (Manga manga : itr)
                                         {
                                             mangaList.add(manga);
                                         }
                                         itr.close();

                                         subscriber.onNext(mangaList);
                                         subscriber.onCompleted();
                                     }
                                     catch (Exception lException)
                                     {
                                         subscriber.onError(lException);
                                     }
                                 });
    }


    /**
     * This function retrieves the source catalog from the database.
     *
     * @return Observable arraylist of sources manga
     */
    public Observable<ArrayList<Manga>> getCatalogList()
    {
        return Observable.create(subscriber ->
                                 {
                                     try
                                     {
                                         ArrayList<Manga> mangaList = new ArrayList<>();
                                         QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase())
                                                                                    .query(Manga.class)
                                                                                    .withSelection("source = ?", SharedPrefs
                                                                                            .getSavedSource())
                                                                                    .query();

                                         for (Manga manga : itr)
                                         {
                                             mangaList.add(manga);
                                         }
                                         itr.close();

                                         subscriber.onNext(mangaList);
                                         subscriber.onCompleted();
                                     }
                                     catch (Exception lException)
                                     {
                                         subscriber.onError(lException);
                                     }
                                 });
    }

    /***
     * This function retrieves an item from the database specified by its url.
     *
     * @param aUrl
     * @return
     */
    public Manga getManga(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("link = ?", aUrl).get();
    }

    /***
     * This function retrieves an item from the dtabase specified by its id.
     *
     * @param aId
     * @return
     */
    public Manga getManga(long aId)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("_id = ?", Long.toString(aId)).get();
    }

    /***
     * This function adds an item to the database.
     *
     * @param aManga
     */
    public void putManga(Manga aManga)
    {
        cupboard().withDatabase(getWritableDatabase()).put(aManga);
    }

    /***
     * This function updates a manga item from the database.
     *
     * @param aManga
     */
    public void updateManga(Manga aManga)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put("alternate", aManga.getAlternate());
        lValues.put("image", aManga.getPicUrl());
        lValues.put("description", aManga.getDescription());
        lValues.put("artist", aManga.getArtist());
        lValues.put("author", aManga.getAuthor());
        lValues.put("genres", aManga.getmGenre());
        lValues.put("status", aManga.getStatus());
        lValues.put("source", aManga.getSource());
        lValues.put("recentChapter", aManga.getRecentChapter());
        lValues.put("link", aManga.getMangaURL());

        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, "link = ?", aManga.getMangaURL());
    }

    /***
     * This function updates an chapter item from the database.
     * @param aChapter
     */
    public void updateChapter(Chapter aChapter)
    {
        MangaLogger.logInfo(TAG, "updateChapter", "Not yet implemented");

        ContentValues lValues = new ContentValues(1);
        lValues.put("url", aChapter.getChapterUrl());
        lValues.put("date", aChapter.getChapterDate());
        lValues.put("mangaTitle", aChapter.getMangaTitle());
        lValues.put("chapterTitle", aChapter.getChapterTitle());
        lValues.put("chapterNumber", aChapter.getChapterNumber());
        lValues.put("currentPage", aChapter.getCurrentPage());
        lValues.put("totalPages", aChapter.getTotalPages());

        cupboard().withDatabase(getWritableDatabase()).update(Chapter.class, lValues, "url = ?", aChapter.getChapterUrl());

    }

    /***
     * This function retrieves a chapter item from the database specified by its url
     * @param aUrl
     * @return
     */
    public Chapter getChapter(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Chapter.class).withSelection("url = ?", aUrl).get();
    }

    /***
     * This function adds a chapter item to the database.
     * @param aChapter
     */
    public void addChapter(Chapter aChapter)
    {
        cupboard().withDatabase(getWritableDatabase()).put(aChapter);
    }

    /***
     * This function removes chapters from a specified manga from the database.
     * @param aManga
     */
    public void removeChapters(Manga aManga)
    {
        cupboard().withDatabase(getWritableDatabase()).delete(Chapter.class, "mangaTitle = ?", aManga.getTitle());
    }

    /***
     * This function unfollows all items from database.
     */
    public void resetLibrary()
    {
        QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase())
                                                   .query(Manga.class)
                                                   .withSelection("NOT following = ?", "0")
                                                   .query();

        for (Manga manga : itr)
        {
            updateMangaUnfollow(manga.getTitle());
        }
        itr.close();
    }

    /***
     * This function removes all chapter items from the database.
     */
    public void resetCachedChapters()
    {
        QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase())
                                                   .query(Manga.class)
                                                   .withSelection("NOT following = ? AND source = ?", "0", new SourceFactory()
                                                           .getSourceName())
                                                   .query();

        for (Manga manga : itr)
        {
            removeChapters(manga);
        }
        itr.close();
    }


}