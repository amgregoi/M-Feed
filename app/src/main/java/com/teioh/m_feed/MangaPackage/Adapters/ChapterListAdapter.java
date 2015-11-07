package com.teioh.m_feed.MangaPackage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus1 on 11/5/2015.
 */
public class ChapterListAdapter extends ArrayAdapter {

    private ArrayList<Chapter> chapters;
    private LayoutInflater mInflater;

    public ChapterListAdapter(Context context, int resource, List<Chapter> objects) {
        super(context, resource, objects);
        chapters = new ArrayList<>(objects);
        mInflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChapterHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.chapter_list_item, null);

            holder = new ChapterHolder();
            holder.mTitle = (TextView) row.findViewById(R.id.mangaTitle);
            holder.cDate = (TextView) row.findViewById(R.id.chapterDate);
            row.setTag(holder);
        } else {
            holder = (ChapterHolder) row.getTag();
        }

        Chapter ch = chapters.get(position);

        //Picasso.with(context).load(tManga.getPicUrl()).resize(139, 200).into(holder.img);
        if(ch == null){
            return row;
        }

        holder.mTitle.setText(ch.getMangaTitle());
        holder.cDate.setText(ch.getChapterDate());
        return row;
    }

    static class ChapterHolder {
        TextView mTitle;
        TextView cDate;
    }
}
