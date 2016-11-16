package com.teioh.m_feed.UI.ReaderActivity;

/**
 * Created by amgregoi on 11/15/16.
 */
public class ReaderEnum
{
    public enum LoadingStatus
    {
        COMPLETE,

        LOADING,

        ERROR;

        public static LoadingStatus getLoadingStatus(int aStatus)
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

        public static int getLoadingStatu(LoadingStatus aStatus)
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
}
