package com.teioh.m_feed.UI.MangaActivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterListAdapter extends ArrayAdapter {

    public final static String TAG = ChapterListAdapter.class.getSimpleName();

    private ArrayList<Chapter> chapters;
    private LayoutInflater mInflater;
    private Context context;
    private int layoutResource;

    public ChapterListAdapter(Context context, int resource, List<Chapter> objects) {
        super(context, resource, objects);
        this.context = context;
        this.chapters = new ArrayList<>(objects);
        this.mInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChapterHolder holder;

        if (row == null) {
            row = mInflater.inflate(layoutResource, null);

            holder = new ChapterHolder();
            holder.mTitle = (TextView) row.findViewById(R.id.mangaTitle);
            holder.cDate = (TextView) row.findViewById(R.id.chapterDate);
            row.setTag(R.string.ChapterListAdapterHolder, holder);
            row.setTag(R.string.ChapterListAdapterTAG, TAG + ":" + position);
        } else {
            holder = (ChapterHolder) row.getTag(R.string.ChapterListAdapterHolder);
        }

        Chapter ch = chapters.get(position);

        if (ch == null) {
            return row;
        }

        //TODO UPDATE SELECTION TO INCLUDE CURRENT SOURCE, UPDATED THE DATABASE BUT DONT WANT TO WIPE IT YET
        Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mTitle = ? AND cNumber = ?", ch.getMangaTitle(), Integer.toString(ch.getChapterNumber()))
                .get();


        if (viewedChapter == null) {
            row.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            row.setBackgroundColor(context.getResources().getColor(R.color.grey));
        }

        holder.mTitle.setText(ch.getMangaTitle());
        holder.cDate.setText(ch.getChapterDate());
        return row;
    }

    static class ChapterHolder {
        TextView mTitle;
        TextView cDate;
    }

    public void reverseChapterListOrder(){
        Collections.reverse(chapters);
        notifyDataSetChanged();
    }
}
