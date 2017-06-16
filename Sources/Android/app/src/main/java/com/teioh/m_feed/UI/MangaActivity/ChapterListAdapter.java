package com.teioh.m_feed.UI.MangaActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.MangaDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChapterListAdapter extends ArrayAdapter
{

    public final static String TAG = ChapterListAdapter.class.getSimpleName();

    private ArrayList<Chapter> mChapterList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutResource;

    /***
     * TODO..
     *
     * @param aContext
     * @param aResource
     * @param aChapterList
     */
    public ChapterListAdapter(Context aContext, int aResource, List<Chapter> aChapterList)
    {
        super(aContext, aResource, aChapterList);
        mContext = aContext;
        mChapterList = new ArrayList<>(aChapterList);
        mInflater = LayoutInflater.from(aContext);
        mLayoutResource = aResource;
    }

    /***
     * TODO..
     *
     * @param aPosition
     * @param aConvertView
     * @param aParent
     * @return
     */
    public View getView(int aPosition, View aConvertView, ViewGroup aParent)
    {
        View lRowView = aConvertView;
        ChapterHolder lHolder;

        if (lRowView == null)
        {
            lRowView = mInflater.inflate(mLayoutResource, null);

            lHolder = new ChapterHolder();
            lHolder.lMangaTitle = (TextView) lRowView.findViewById(R.id.mangaTitle);
            lHolder.lChapterDate = (TextView) lRowView.findViewById(R.id.chapterDate);
            lRowView.setTag(R.string.ChapterListAdapterHolder, lHolder);
            lRowView.setTag(R.string.ChapterListAdapterTAG, TAG + ":" + aPosition);
        }
        else
        {
            lHolder = (ChapterHolder) lRowView.getTag(R.string.ChapterListAdapterHolder);
        }

        Chapter lChapter = mChapterList.get(aPosition);

        if (lChapter == null)
        {
            return lRowView;
        }

        Chapter lViewedChapter = MangaDB.getInstance().getChapter(lChapter.getChapterUrl());


        if (lViewedChapter != null)
        {
            lRowView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorPrimary));
        }
        else
        {
            lRowView.setBackgroundColor(mContext.getResources().getColor(R.color.charcoal));
        }

        lHolder.lMangaTitle.setText(lChapter.getChapterTitle());
        lHolder.lChapterDate.setText(lChapter.getChapterDate());
        return lRowView;
    }

    /***
     * TODO..
     */
    static class ChapterHolder
    {
        TextView lMangaTitle;
        TextView lChapterDate;
    }

    /***
     * TODO..
     */
    public void reverseChapterListOrder()
    {
        Collections.reverse(mChapterList);
        notifyDataSetChanged();
    }
}
