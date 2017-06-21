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

    private MangaTable mMangaTable;
    private ChapterTable mChapterTable;

    public MangaDB(Context aContext)
    {
        super(aContext, sDB_NAME, null, sDATABASE_VERSION);
        mContext = aContext;

        mMangaTable = new MangaTable();
        mChapterTable = new ChapterTable();
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
     * @param aTitle
     * @param aValue
     */
    public void updateMangaFollow(String aTitle, int aValue)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(mMangaTable.Following, aValue);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, mMangaTable.Title + " = ?", aTitle);
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
                                                                                       .withSelection("NOT " + mMangaTable.Following + " = ? AND " + mMangaTable.Source + " = ?", "0", new SourceFactory()
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
                                                                                       .withSelection(mMangaTable.Source + " = ?", SharedPrefs
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
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection(mMangaTable.ID + " = ?", Long.toString(aId))
                         .get();
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
        lValues.put(mMangaTable.Alternate, aManga.getAlternate());
        lValues.put(mMangaTable.Image, aManga.getPicUrl());
        lValues.put(mMangaTable.Description, aManga.getDescription());
        lValues.put(mMangaTable.Artist, aManga.getArtist());
        lValues.put(mMangaTable.Author, aManga.getAuthor());
        lValues.put(mMangaTable.Genres, aManga.getmGenre());
        lValues.put(mMangaTable.Status, aManga.getStatus());
        lValues.put(mMangaTable.Source, aManga.getSource());
        lValues.put(mMangaTable.RecentChapter, aManga.getRecentChapter());
        lValues.put(mMangaTable.URL, aManga.getMangaURL());

        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, mMangaTable.URL + " = ?", aManga.getMangaURL());

        Manga test = getManga(aManga.getMangaURL());
        test = null;
    }

    /***
     * This function retrieves an item from the database specified by its url.
     *
     * @param aUrl
     * @return
     */
    public Manga getManga(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection(mMangaTable.URL + " = ?", aUrl).get();
    }

    /***
     * This function updates an chapter item from the database.
     * @param aChapter
     */
    public void updateChapter(Chapter aChapter)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(mChapterTable.URL, aChapter.getChapterUrl());
        lValues.put(mChapterTable.Date, aChapter.getChapterDate());
        lValues.put(mChapterTable.MangaTitle, aChapter.getMangaTitle());
        lValues.put(mChapterTable.ChapterTitle, aChapter.getChapterTitle());
        lValues.put(mChapterTable.ChapterNumber, aChapter.getChapterNumber());
        lValues.put(mChapterTable.CurrentPage, aChapter.getCurrentPage());
        lValues.put(mChapterTable.TotalPages, aChapter.getTotalPages());

        cupboard().withDatabase(getWritableDatabase()).update(Chapter.class, lValues, mChapterTable.URL + " = ?", aChapter.getChapterUrl());
    }

    /***
     * This function retrieves a chapter item from the database specified by its url
     * @param aUrl
     * @return
     */
    public Chapter getChapter(String aUrl)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Chapter.class).withSelection(mChapterTable.URL + " = ?", aUrl).get();
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
        QueryResultIterable<Manga> lQuery = cupboard().withDatabase(getReadableDatabase())
                                                      .query(Manga.class)
                                                      .withSelection("NOT " + mMangaTable.Following + " = ?", "0")
                                                      .query();

        for (Manga iManga : lQuery)
        {
            updateMangaUnfollow(iManga.getTitle());
        }
        lQuery.close();
    }

    /***
     * This function updates the follow status of an item in the database (unfollow)
     *
     * @param aTitle
     */
    public void updateMangaUnfollow(String aTitle)
    {
        ContentValues lValues = new ContentValues(1);
        lValues.put(mMangaTable.Following, 0);
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, lValues, mMangaTable.Title + " = ?", aTitle);
    }

    /***
     * This function removes all chapter items from the database.
     */
    public void resetCachedChapters()
    {
        QueryResultIterable<Manga> lQuery = cupboard().withDatabase(getReadableDatabase())
                                                      .query(Manga.class)
                                                      .withSelection("NOT" + mMangaTable.Following + " = ? AND " + mMangaTable.Source + " = ?", "0", new SourceFactory()
                                                              .getSourceName())
                                                      .query();

        for (Manga iManga : lQuery)
        {
            removeChapters(iManga);
        }
        lQuery.close();
    }

    /***
     * This function removes chapters from a specified manga from the database.
     * @param aManga
     */
    public void removeChapters(Manga aManga)
    {
        cupboard().withDatabase(getWritableDatabase()).delete(Chapter.class, mChapterTable.MangaTitle + " = ?", aManga.getTitle());
    }

    /***
     * This inner class defines the sql column names for the Manga Table
     */
    static class MangaTable
    {
        public String ID = "_id";
        public String Title = "title";
        public String Alternate = "alternate";
        public String Image = "image";
        public String Description = "description";
        public String Artist = "artist";
        public String Author = "author";
        public String Genres = "genres";
        public String Status = "status";
        public String Source = "source";
        public String RecentChapter = "recentChapter";
        public String URL = "link";
        public String Following = "following";
    }

    /***
     * This inner class defines the sql column names for the Chapter Table
     */
    static class ChapterTable
    {
        public String TotalPages = "totalPages";
        public String CurrentPage = "currentPage";
        public String ChapterNumber = "chapterNumber";
        public String ChapterTitle = "chapterTitle";
        public String MangaTitle = "mangaTitle";
        public String Date = "date";
        public String URL = "url";
    }

}