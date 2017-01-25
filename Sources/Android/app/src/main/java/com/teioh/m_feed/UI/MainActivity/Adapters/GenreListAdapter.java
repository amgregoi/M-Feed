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

public class GenreListAdapter extends BaseAdapter
{

    private ArrayList<String> mGenreList = null;
    private int mGenreSearchStatus[];
    private LayoutInflater mInflater;

    /***
     * TODO..
     *
     * @param aContext
     * @param aGenres
     */
    public GenreListAdapter(Context aContext, ArrayList<String> aGenres)
    {
        mGenreList = aGenres;
        mGenreSearchStatus = new int[aGenres.size()];
        mInflater = LayoutInflater.from(aContext);
    }

    /***
     * TODO..
     *
     * @return
     */
    public int getCount()
    {
        return mGenreList.size();
    }

    /***
     * TODO..
     *
     * @param aPosition
     * @return
     */
    public Object getItem(int aPosition)
    {
        return mGenreList.get(aPosition);
    }

    /***
     * TODO..
     *
     * @param aPosition
     * @return
     */
    public long getItemId(int aPosition)
    {
        return aPosition;
    }

    /***
     * TODO..
     *
     * @param aPosition
     * @param aConvertView
     */
    public void updateItem(int aPosition, View aConvertView)
    {
        Context lContext = aConvertView.getContext();

        if (aPosition >= 0)
        {
            View row = aConvertView;
            GenreHolder holder = (GenreHolder) row.getTag();
            mGenreSearchStatus[aPosition] = (mGenreSearchStatus[aPosition] + 1) % 3;

            if (mGenreSearchStatus[aPosition] == 0)
            {
                holder.lSymbolImageView.setImageDrawable(null);
                holder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.light_charcoal));
            }
            else if (mGenreSearchStatus[aPosition] == 1)
            {
                holder.lSymbolImageView.setImageDrawable(lContext.getDrawable(R.drawable.ic_add_white_18dp));
                holder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.green));
            }
            else
            {
                holder.lSymbolImageView.setImageDrawable(lContext.getDrawable(R.drawable.ic_remove_white_18dp));
                holder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.red));
            }
            row.invalidate();
        }
    }

    /***
     * TODO..
     */
    public void resetGenreFilters()
    {
        mGenreSearchStatus = new int[mGenreList.size()];
    }

    /***
     * TODO..
     *
     * @param aStatus
     * @return
     */
    public List<String> getGenreListByStatus(int aStatus)
    {
        List<String> result = new ArrayList<>();
        for (int iIndex = 0; iIndex < mGenreList.size(); iIndex++)
        {
            if (mGenreSearchStatus[iIndex] == aStatus) result.add(mGenreList.get(iIndex));
        }
        return result;
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
        Context lContext = aParent.getContext();
        View lRowView = aConvertView;
        GenreHolder lGenreHolder;

        if (lRowView == null)
        {
            lRowView = mInflater.inflate(R.layout.main_genre_grid_item, null);

            lGenreHolder = new GenreHolder();
            lGenreHolder.lTextView = (TextView) lRowView.findViewById(R.id.genre_title);
            lGenreHolder.lSymbolImageView = (ImageView) lRowView.findViewById(R.id.genre_symbol);
            lGenreHolder.lLayout = (RelativeLayout) lRowView.findViewById(R.id.card_view_container);
            lGenreHolder.lCard = (CardView) lRowView.findViewById(R.id.genre_card_view);
            lRowView.setTag(lGenreHolder);
        }
        else
        {
            lGenreHolder = (GenreHolder) lRowView.getTag();
        }

        String lCurGenre = mGenreList.get(aPosition);

        if (lCurGenre == null)
        {
            return lRowView;
        }

        if (mGenreSearchStatus[aPosition] == 0)
        {
            lGenreHolder.lSymbolImageView.setImageDrawable(null);
            lGenreHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.light_charcoal));
        }
        else if (mGenreSearchStatus[aPosition] == 1)
        {
            lGenreHolder.lSymbolImageView.setImageDrawable(lContext.getResources().getDrawable(R.drawable.ic_add_white_18dp));
            lGenreHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.green));
        }
        else
        {
            lGenreHolder.lSymbolImageView.setImageDrawable(lContext.getResources().getDrawable(R.drawable.ic_remove_white_18dp));
            lGenreHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.red));
        }

        lGenreHolder.lTextView.setText(lCurGenre);
        return lRowView;
    }

    /***
     * TODO..
     */
    static class GenreHolder
    {
        TextView lTextView;
        ImageView lSymbolImageView;
        RelativeLayout lLayout;
        CardView lCard;
    }

}
