package com.teioh.m_feed.UI.SearchActivity.Adapter;

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

    private ArrayList<String> data = null;
    private int dataStatus[];
    private LayoutInflater mInflater;
    private Context context;

    public GenreListAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
        dataStatus = new int[data.size()];
        mInflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateItem(int position, View convertView) {
        if(position >= 0) {
            View row = convertView;
            GenreHolder holder = (GenreHolder) row.getTag();
            dataStatus[position] = (dataStatus[position] + 1) % 3;

            if (dataStatus[position] == 0) {
                holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_search_white_18dp));
                holder.content.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            } else if (dataStatus[position] == 1) {
                holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_white_18dp));
                holder.content.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_white_18dp));
                holder.content.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            row.invalidate();
        }
    }

    //returns list of genres with a certain status, so we know what to
    //look for (status = 1), filter out (status = 2), and ignore (status = 0)
    public List<String> getGenreListByStatus(int status) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (dataStatus[i] == status) result.add(data.get(i));
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

        String curGenre = data.get(position);

        if (curGenre == null) {
            return row;
        }

        if (dataStatus[position] == 0) {
            holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_search_white_18dp));
            row.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
        } else if (dataStatus[position] == 1) {
            holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_done));
            row.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else {
            holder.symbol.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_white_18dp));
            row.setBackgroundColor(context.getResources().getColor(R.color.red));
        }

        //Picasso.with(context).load(tManga.getPicUrl()).resize(139, 200).into(holder.img);
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
