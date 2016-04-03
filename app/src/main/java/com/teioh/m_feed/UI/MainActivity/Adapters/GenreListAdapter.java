package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.List;

public class GenreListAdapter extends BaseAdapter {

    private ArrayList<String> mData = null;
    private int mDataStatus[];
    private LayoutInflater mInflater;
    private Context mContext;

    public GenreListAdapter(Context context, ArrayList<String> data) {
        mContext = context;
        mData = data;
        mDataStatus = new int[data.size()];
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateItem(int position, View convertView) {
        if(position >= 0) {
            View row = convertView;
            GenreHolder holder = (GenreHolder) row.getTag();
            mDataStatus[position] = (mDataStatus[position] + 1) % 3;

            if (mDataStatus[position] == 0) {
                holder.symbol.setImageDrawable(null);
                holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.light_charcoal));
            } else if (mDataStatus[position] == 1) {
                holder.symbol.setImageDrawable(mContext.getDrawable(R.drawable.ic_add_white_18dp));
                holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            } else {
                holder.symbol.setImageDrawable(mContext.getDrawable(R.drawable.ic_remove_white_18dp));
                holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            row.invalidate();
        }
    }
    public void resetGenreFilters(){
        mDataStatus = new int[mData.size()];
    }

    //returns list of genres with a certain status, so we know what to
    //look for (status = 1), filter out (status = 2), and ignore (status = 0)
    public List<String> getGenreListByStatus(int status) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (mDataStatus[i] == status) result.add(mData.get(i));
        }
        return result;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GenreHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.search_genre_item, null);

            holder = new GenreHolder();
            holder.txt = (TextView) row.findViewById(R.id.genre_title);
            holder.symbol = (ImageView) row.findViewById(R.id.genre_symbol);
            holder.content = (RelativeLayout) row.findViewById(R.id.card_view_container);
            holder.card = (CardView) row.findViewById(R.id.genre_card_view);
            row.setTag(holder);
        } else {
            holder = (GenreHolder) row.getTag();
        }

        String curGenre = mData.get(position);

        if (curGenre == null) {
            return row;
        }

        if (mDataStatus[position] == 0) {
            holder.symbol.setImageDrawable(null);
            holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.light_charcoal));
        } else if (mDataStatus[position] == 1) {
            holder.symbol.setImageDrawable(mContext.getDrawable(R.drawable.ic_add_white_18dp));
            holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        } else {
            holder.symbol.setImageDrawable(mContext.getDrawable(R.drawable.ic_remove_white_18dp));
            holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        }

        holder.txt.setText(curGenre);
        return row;
    }

    static class GenreHolder {
        TextView txt;
        ImageView symbol;
        RelativeLayout content;
        CardView card;
    }

}
