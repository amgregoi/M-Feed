package com.teioh.m_feed;

import com.teioh.m_feed.WebSources.SourceBase;
import com.teioh.m_feed.WebSources.Sources.Batoto;
import com.teioh.m_feed.WebSources.Sources.MangaEden;
import com.teioh.m_feed.WebSources.Sources.MangaHere;
import com.teioh.m_feed.WebSources.Sources.MangaJoy;
import com.teioh.m_feed.WebSources.Sources.ReadLight;
import com.teioh.m_feed.WebSources.Sources.WuxiaWorld;

/**
 * Created by amgregoi on 11/19/16.
 */
public class MangaEnums
{
    /***
     * This enum is for the various manga filter status'
     */
    public enum eFilterStatus
    {
        NONE(0),

        READING(1),

        ON_HOLD(3),

        COMPLETE(2),

        FOLLOWING(5);

        private int mValue;

        eFilterStatus(int aValue)
        {
            mValue = aValue;
        }

        public int getValue()
        {
            return mValue;
        }

    }


    /***
     * This enum is for the various loading status'
     */
    public enum eLoadingStatus
    {
        COMPLETE,

        LOADING,

        ERROR,

        REFRESH;

        public static eLoadingStatus getLoadingStatus(int aStatus)
        {
            switch (aStatus)
            {
                case 0:
                    return LOADING;
                case 1:
                    return COMPLETE;
                case 2:
                    return REFRESH;
                default:
                    return ERROR;
            }
        }

        public static int getLoadingStatus(eLoadingStatus aStatus)
        {
            switch (aStatus)
            {
                case LOADING:
                    return 0;
                case COMPLETE:
                    return 1;
                case REFRESH:
                    return 2;
                default:
                    return 3;
            }
        }
    }

    public enum eSourceType
    {
        MANGA,
        NOVEL
    }

    /***
     * This enum is for the various sources.
     */
    public enum eSource
    {
        Batoto(new Batoto()),
        MangaEden(new MangaEden()),
        MangaHere(new MangaHere()),
        MangaJoy(new MangaJoy()),
//        WuxiaWorld(new WuxiaWorld()),
        ReadLight(new ReadLight());

        SourceBase lSource;

        eSource(SourceBase aSource)
        {
            lSource = aSource;
        }

        public SourceBase getSource()
        {
            return lSource;
        }
    }

    /***
     * This enum is for the various follow status types.
     */
    public enum eFollowType
    {
        Reading, Completed, On_Hold;

        @Override
        public String toString()
        {
            return super.toString().replace("_", " ");
        }
    }
}
