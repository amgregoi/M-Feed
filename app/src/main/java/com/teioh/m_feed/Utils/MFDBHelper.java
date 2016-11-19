package com.teioh.m_feed.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.teioh.m_feed.MFeedApplication;
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

public class MFDBHelper extends SQLiteOpenHelper
{
    public static final String TAG = MFDBHelper.class.getSimpleName();

    public static final int sDATABASE_VERSION = 1;
    private static final String sDB_PATH = "/data/data/com.teioh.m_feed/databases/";
    private static final String sDB_NAME = "MangaFeed.db";
    private static MFDBHelper sInstance;
    private Context mContext;

    public MFDBHelper(Context aContext)
    {
        super(aContext, sDB_NAME, null, sDATABASE_VERSION);
        mContext = aContext;
    }

    /***
     * TODO...
     */
    public static synchronized MFDBHelper getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new MFDBHelper(MFeedApplication.getInstance());
        }
        return sInstance;
    }

    /***
     * TODO...
     */
    public void onCreate(SQLiteDatabase aDb)
    {
        cupboard().withDatabase(aDb).createTables();
    }

    /***
     * TODO...
     */
    public void onUpgrade(SQLiteDatabase aDb, int aOldVersion, int aNewVersion)
    {
        cupboard().withDatabase(aDb).upgradeTables();
    }

    /***
     * TODO...
     */
    public void createDatabase()
    {
        createDB();
    }

    /***
     * TODO...
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
     * TODO...
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
     * TODO...
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
     * TODO...
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
     * TODO...
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
     * TODO...
     *
     * @return Observable arraylist of users followed manga
     */
    public Observable<ArrayList<Manga>> getFollowedList()
    {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Manga>>()
        {
            @Override
            public void call(Subscriber<? super ArrayList<Manga>> subscriber)
            {
                try
                {
                    ArrayList<Manga> mangaList = new ArrayList<>();
                    QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("NOT following = ? AND source = ?", "0", new SourceFactory().getSourceName()).query();

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
            }
        });
    }


    /**
     * TODO...
     *
     * @return Observable arraylist of users followed manga
     */
    public Observable<ArrayList<Manga>> getLibraryList()
    {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Manga>>()
        {
            @Override
            public void call(Subscriber<? super ArrayList<Manga>> subscriber)
            {
                try
                {
                    ArrayList<Manga> mangaList = new ArrayList<>();
                    QueryResultIterable<Manga> itr = cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("source = ?", SharedPrefs.getSavedSource()).query();

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
            }
        });
    }

    /***
     * TODO...
     *
     * @param aUrl
     * @param aSource
     * @return
     */
    public Manga getManga(String aUrl, String aSource)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("link = ? AND source = ?", aUrl, aSource).get();
    }

    /***
     * TODO...
     *
     * @param aId
     * @return
     */
    public Manga getManga(long aId)
    {
        return cupboard().withDatabase(getReadableDatabase()).query(Manga.class).withSelection("_id = ?", Long.toString(aId)).get();
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    public void putManga(Manga aManga)
    {
        cupboard().withDatabase(getWritableDatabase()).put(aManga);
    }

    /***
     * TODO...
     *
     * @param aValues
     * @param aUrl
     */
    public void updateManga(ContentValues aValues, String aUrl)
    {
        cupboard().withDatabase(getWritableDatabase()).update(Manga.class, aValues, "link = ?", aUrl);
    }


}