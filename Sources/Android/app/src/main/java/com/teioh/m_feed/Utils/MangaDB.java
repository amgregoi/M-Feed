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
        SQLiteDatabase lDb = null;

        try
        {
            File lDatabase = mContext.getDatabasePath(sDB_NAME);
            if (lDatabase.exists())
            {
                lDb = SQLiteDatabase.openDatabase(lDatabase.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                lDb.setLocale(Locale.getDefault());
                lDb.setVersion(1);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage(), "Database not found");
        }

        if (lDb != null)
        {
            lDb.close();
        }
        return lDb != null;
    }

    /***
     * This function copies the database provided with the apk.
     */
    private void copyDBFromResource()
    {
        String lFilePath = sDB_PATH + sDB_NAME;
        try
        {
            InputStream lInputStream = MFeedApplication.getInstance().getAssets().open(sDB_NAME);
            OutputStream lOutStream = new FileOutputStream(lFilePath);

            byte[] lBuffer = new byte[1024];
            int lLength;
            while ((lLength = lInputStream.read(lBuffer)) > 0)
            {
                lOutStream.write(lBuffer, 0, lLength);
            }

            lOutStream.flush();
            lOutStream.close();
            lInputStream.close();
        }
        catch (IOException aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the follow status of an item in the database
     *
     * @param aUrl
     * @param aValue
     */
    public void updateMangaFollow(String aUrl, int aValue)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(MangaTable.Following, aValue);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, MangaTable.URL + " = ?", aUrl);
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
                                         ArrayList<Manga> lMangaList = new ArrayList<>();
                                         QueryResultIterable<Manga> lQuery = cupboard().withDatabase(getReadableDatabase())
                                                                                       .query(Manga.class)
                                                                                       .distinct().groupBy(MangaTable.Title)
                                                                                       .withSelection("NOT " + MangaTable.Following + " = ? AND " + MangaTable.Source + " = ?", "0", SourceFactory
                                                                                               .getInstance()
                                                                                               .getSourceName())
                                                                                       .query();

                                         for (Manga iManga : lQuery)
                                         {
                                             lMangaList.add(iManga);
                                         }
                                         lQuery.close();

                                         subscriber.onNext(lMangaList);
                                         subscriber.onCompleted();
                                     }
                                     catch (Exception aException)
                                     {
                                         subscriber.onError(aException);
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
                                         ArrayList<Manga> lMangaList = new ArrayList<>();
                                         QueryResultIterable<Manga> lQuery = cupboard().withDatabase(getReadableDatabase())
                                                                                       .query(Manga.class)
                                                                                       .withSelection(MangaTable.Source + " = ?", SharedPrefs
                                                                                               .getSavedSource())
                                                                                       .query();

                                         for (Manga iManga : lQuery)
                                         {
                                             lMangaList.add(iManga);
                                         }
                                         lQuery.close();

                                         subscriber.onNext(lMangaList);
                                         subscriber.onCompleted();
                                     }
                                     catch (Exception lException)
                                     {
                                         subscriber.onError(lException);
                                     }
                                 });
    }

    /***
     * This function retrieves an item from the dtabase specified by its id.
     *
     * @param aId
     * @return
     */
    public Manga getManga(long aId)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection(MangaTable.ID + " = ?", Long.toString(aId))
                         .get();
    }

    /***
     * This function adds an item to the database.
     *
     * @param aManga
     */
    public void putManga(Manga aManga)
    {
        if (cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection(MangaTable.URL + " = ?", aManga.getMangaURL())
                      .get() == null)
            cupboard().withDatabase(getWritableDatabase()).put(aManga);
        else
            updateManga(aManga);
    }

    /***
     * This function updates a manga item from the database.
     *
     * @param aManga
     */
    public void updateManga(Manga aManga)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(MangaTable.Alternate, aManga.getAlternate());
        lValues.put(MangaTable.Image, aManga.getPicUrl());
        lValues.put(MangaTable.Description, aManga.getDescription());
        lValues.put(MangaTable.Artist, aManga.getArtist());
        lValues.put(MangaTable.Author, aManga.getAuthor());
        lValues.put(MangaTable.Genres, aManga.getmGenre());
        lValues.put(MangaTable.Status, aManga.getStatus());
        lValues.put(MangaTable.Source, aManga.getSource());
        lValues.put(MangaTable.RecentChapter, aManga.getRecentChapter());
        lValues.put(MangaTable.URL, aManga.getMangaURL());
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, MangaTable.URL + " = ?", aManga.getMangaURL());

    }

    /***
     * This function retrieves an item from the database specified by its url.
     *
     * @param aUrl
     * @return
     */
    public Manga getManga(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection(MangaTable.URL + " = ?", aUrl).get();
    }

    /***
     * This function updates an chapter item from the database.
     * @param aChapter
     */
    public void updateChapter(Chapter aChapter)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(ChapterTable.URL, aChapter.getChapterUrl());
        lValues.put(ChapterTable.Date, aChapter.getChapterDate());
        lValues.put(ChapterTable.MangaTitle, aChapter.getMangaTitle());
        lValues.put(ChapterTable.ChapterTitle, aChapter.getChapterTitle());
        lValues.put(ChapterTable.ChapterNumber, aChapter.getChapterNumber());
        lValues.put(ChapterTable.CurrentPage, aChapter.getCurrentPage());
        lValues.put(ChapterTable.TotalPages, aChapter.getTotalPages());

        cupboard().withDatabase(getWritableDatabase()).update(Chapter.class, lValues, ChapterTable.URL + " = ?", aChapter.getChapterUrl());
    }

    /***
     * This function retrieves a chapter item from the database specified by its url
     * @param aUrl
     * @return
     */
    public Chapter getChapter(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Chapter.class).withSelection(ChapterTable.URL + " = ?", aUrl).get();
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
     * This function unfollows all items from database.
     */
    public void resetLibrary()
    {
        getWritableDatabase().execSQL("UPDATE Manga set following = 0 WHERE following > 0");
    }

    /***
     * This function updates the follow status of an item in the database (unfollow)
     *
     * @param aUrl
     */
    public void updateMangaUnfollow(String aUrl)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(MangaTable.Following, 0);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, MangaTable.URL + " = ?", aUrl);
    }

    /***
     * This function removes all chapter items from the database.
     */
    public void resetCachedChapters()
    {
        getWritableDatabase().execSQL("DELETE FROM Chapter");
    }

    /***
     * This function removes chapters from a specified manga from the database.
     * @param aManga
     */
    public void removeChapters(Manga aManga)
    {
        cupboard().withDatabase(getWritableDatabase()).delete(Chapter.class, ChapterTable.MangaTitle + " = ?", aManga.getTitle());
    }


    /***
     * This inner class defines the sql column names for the Manga Table
     */
    static class MangaTable
    {
        public final static String ID = "_id";
        public final static String Title = "title";
        public final static String Alternate = "alternate";
        public final static String Image = "image";
        public final static String Description = "description";
        public final static String Artist = "artist";
        public final static String Author = "author";
        public final static String Genres = "genres";
        public final static String Status = "status";
        public final static String Source = "source";
        public final static String RecentChapter = "recentChapter";
        public final static String URL = "link";
        public final static String Following = "following";
    }

    /***
     * This inner class defines the sql column names for the Chapter Table
     */
    static class ChapterTable
    {
        public final static String TotalPages = "totalPages";
        public final static String CurrentPage = "currentPage";
        public final static String ChapterNumber = "chapterNumber";
        public final static String ChapterTitle = "chapterTitle";
        public final static String MangaTitle = "mangaTitle";
        public final static String Date = "date";
        public final static String URL = "url";
    }

}