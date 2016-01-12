package com.teioh.m_feed.Utils.OttoBus;

/**
 * Created by amgregoi on 1/11/16.
 */
public class ChangeChapter {
    private boolean isNext;

    public ChangeChapter(boolean isNext){
        this.isNext = isNext;
    }

    public boolean getIsNext(){
        return isNext;
    }
}
