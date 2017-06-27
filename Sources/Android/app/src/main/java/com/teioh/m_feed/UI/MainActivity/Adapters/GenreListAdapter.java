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
     * This is the constructor for the Genre List Adapter.
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
     * This function will return the count of Genre List.
     *
     * @return
     */
    public int getCount()
    {
        return mGenreList.size();
    }

    /***
     * This function returns an item in the Genre List at the specified location.
     *
     * @param aPosition The position of an item to be retrieved.
     * @return
     */
    public Object getItem(int aPosition)
    {
        return mGenreList.get(aPosition);
    }

    /***
     * This function returns the ID of an item in the Genre List at the specified location.
     *
     * @param aPosition The position of an item to retrieve its ID.
     * @return
     */
    public long getItemId(int aPosition)
    {
        return aPosition;
    }

    /***
     * This function gets the view of an item at the specified position.
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
            lRowView = mInflater.inflate(R.layout.main_search_dialog_grid_item, null);

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
     * This function updates and invalidates the view of a specific item at the specified position.
     *
     * @param aPosition The position of the item to be updated.
     * @param aConvertView The view of the item to be refreshed.
     */
    public void updateItem(int aPosition, View aConvertView)
    {
        Context lContext = aConvertView.getContext();

        if (aPosition >= 0)
        {
            View lRow = aConvertView;
            GenreHolder lHolder = (GenreHolder) lRow.getTag();
            mGenreSearchStatus[aPosition] = (mGenreSearchStatus[aPosition] + 1) % 3;

            if (mGenreSearchStatus[aPosition] == 0)
            {
                lHolder.lSymbolImageView.setImageDrawable(null);
                lHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.light_charcoal));
            }
            else if (mGenreSearchStatus[aPosition] == 1)
            {
                lHolder.lSymbolImageView.setImageDrawable(lContext.getDrawable(R.drawable.ic_add_white_18dp));
                lHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.green));
            }
            else
            {
                lHolder.lSymbolImageView.setImageDrawable(lContext.getDrawable(R.drawable.ic_remove_white_18dp));
                lHolder.lLayout.setBackgroundColor(lContext.getResources().getColor(R.color.red));
            }

            lRow.invalidate();
        }
    }

    /***
     * This function will re-initialize the Genre status array.
     */
    public void resetGenreFilters()
    {
        mGenreSearchStatus = new int[mGenreList.size()];
    }

    /***
     * This function returns the list of genres based on their status (INCLUDE, EXCLUDE, NEUTRAL)
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
     * This class acts as holder for Genre item data.
     */
    static class GenreHolder
    {
        TextView lTextView;
        ImageView lSymbolImageView;
        RelativeLayout lLayout;
        CardView lCard;
    }

}
