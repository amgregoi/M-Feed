package com.teioh.m_feed;

import com.teioh.m_feed.WebSources.SourceBase;
import com.teioh.m_feed.WebSources.Sources.Batoto;
import com.teioh.m_feed.WebSources.Sources.MangaEden;
import com.teioh.m_feed.WebSources.Sources.MangaHere;
import com.teioh.m_feed.WebSources.Sources.MangaJoy;
import com.teioh.m_feed.WebSources.Sources.MangaPark;

/**
 * Created by amgregoi on 11/19/16.
 */
public class MangaEnums
{
    /***
     * TODO..
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
     * TODO..
     */
    public enum eLoadingStatus
    {
        COMPLETE,

        LOADING,

        ERROR;

        public static eLoadingStatus getLoadingStatus(int aStatus)
        {
            switch (aStatus)
            {
                case 0:
                    return LOADING;
                case 1:
                    return COMPLETE;
                default:
                    return ERROR;
            }
        }

        public static int getLoadingStatu(eLoadingStatus aStatus)
        {
            switch (aStatus)
            {
                case LOADING:
                    return 0;
                case COMPLETE:
                    return 1;
                default:
                    return 2;
            }
        }
    }

    /***
     * TODO..
     */
    public enum eSource
    {
        Batoto(new Batoto()),
        MangaEden(new MangaEden()),
        MangaHere(new MangaHere()),
        MangaJoy(new MangaJoy()),
        MangaPark(new MangaPark());

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
     * TODO..
     */
    public enum eFollowType
    {
        Reading, Completed, On_Hold
    }
}
