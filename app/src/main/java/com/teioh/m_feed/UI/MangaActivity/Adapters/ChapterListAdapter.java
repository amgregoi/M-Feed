package com.teioh.m_feed.UI.MangaActivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.MFDBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterListAdapter extends ArrayAdapter {

    public final static String TAG = ChapterListAdapter.class.getSimpleName();

    private ArrayList<Chapter> mChapterList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutResource;

    public ChapterListAdapter(Context aContext, int aResource, List<Chapter> aChapterList) {
        super(aContext, aResource, aChapterList);
        mContext = aContext;
        mChapterList = new ArrayList<>(aChapterList);
        mInflater = LayoutInflater.from(aContext);
        mLayoutResource = aResource;
    }

    public View getView(int aPosition, View aConvertView, ViewGroup aParent) {
        View lRowView = aConvertView;
        ChapterHolder lHolder;

        if (lRowView == null) {
            lRowView = mInflater.inflate(mLayoutResource, null);

            lHolder = new ChapterHolder();
            lHolder.lMangaTitle = (TextView) lRowView.findViewById(R.id.mangaTitle);
            lHolder.lChapterDate = (TextView) lRowView.findViewById(R.id.chapterDate);
            lRowView.setTag(R.string.ChapterListAdapterHolder, lHolder);
            lRowView.setTag(R.string.ChapterListAdapterTAG, TAG + ":" + aPosition);
        } else {
            lHolder = (ChapterHolder) lRowView.getTag(R.string.ChapterListAdapterHolder);
        }

        Chapter lChapter = mChapterList.get(aPosition);

        if (lChapter == null) {
            return lRowView;
        }

        Chapter lViewedChapter = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mangaTitle = ? AND chapterNumber = ?", lChapter.getMangaTitle(), Integer.toString(lChapter.getChapterNumber()))
                .get();



        if (lViewedChapter != null) {
            lRowView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorPrimary));
        }else{
            lRowView.setBackgroundColor(mContext.getResources().getColor(R.color.charcoal));
        }

        lHolder.lMangaTitle.setText(lChapter.getChapterTitle());
        lHolder.lChapterDate.setText(lChapter.getChapterDate());
        return lRowView;
    }

    static class ChapterHolder {
        TextView lMangaTitle;
        TextView lChapterDate;
    }

    public void reverseChapterListOrder(){
        Collections.reverse(mChapterList);
        notifyDataSetChanged();
    }
}
