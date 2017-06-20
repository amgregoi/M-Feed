package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.GenreListAdapter;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class FilterDialogFragment extends DialogFragment
{
    public final static String TAG = FilterDialogFragment.class.getSimpleName();

    @Bind(R.id.genreList)
    GridView mGenreGridView;
    @Bind(R.id.genre_search_button)
    Button mSearchButton;
    @Bind(R.id.genre_cancel_button)
    Button mCancelButton;
    @Bind(R.id.genre_clear_button)
    Button mClearButton;

    private GenreListAdapter mAdapter;

    /***
     * This function creates and returns a new instance of the fragment.
     *
     * @return
     */
    public static DialogFragment getnewInstance()
    {
        DialogFragment dialog = new FilterDialogFragment();
        return dialog;
    }

    /***
     * This function initializes and creates the view of the fragment.
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.main_search_dialog, aContainer, false);
        ButterKnife.bind(this, lView);

        mAdapter = new GenreListAdapter(getContext(), new ArrayList<>(Arrays.asList(new SourceFactory().getSource().genres)));
        registerAdapter(mAdapter);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        return lView;
    }

    /***
     * This function is called when the fragment is destroyed for cleanup.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /***
     * This function registers the adapter for the Genre grid view.
     *
     * @param aAdapter
     */
    private void registerAdapter(BaseAdapter aAdapter)
    {
        mGenreGridView.setAdapter(aAdapter);
        mGenreGridView
                .setOnItemClickListener((aParent, aView, aPosition, aId) -> ((GenreListAdapter) aAdapter).updateItem(aPosition, aView));
        mSearchButton.setOnClickListener(aView -> performSearch());
        mCancelButton.setOnClickListener(aView ->
                                         {
                                             FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_CANCELED, null);
                                             getDialog().dismiss();
                                         });
        mClearButton.setOnClickListener(aView ->
                                        {
                                            mAdapter = new GenreListAdapter(getContext(), new ArrayList<>(Arrays.asList(new SourceFactory()
                                                                                                                                .getSource().genres)));
                                            registerAdapter(mAdapter);
                                        });
    }

    /***
     * This function performs the filter based on the selection of the genre grid view.
     */
    private void performSearch()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        StringBuilder lSelection = new StringBuilder();
        List<String> lSelectionArgs = new ArrayList<>();

        lSelection.append("source" + " = ?");
        lSelectionArgs.add(new SourceFactory().getSourceName());

        List<String> lKeepList = mAdapter.getGenreListByStatus(1);
        List<String> lRemoveList = mAdapter.getGenreListByStatus(2);

        //search with no parameters selected, reset to original data
        if (lKeepList.size() == 0 && lRemoveList.size() == 0)
        {
            FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, null);
            getDialog().dismiss();
        }
        else
        {
            MangaLogger.logInfo(TAG, lMethod, "Starting filter search");

            for (String iString : lKeepList)
            {
                lSelection.append(" AND ");
                lSelection.append("genres" + " LIKE ?");
                lSelectionArgs.add("%" + iString.replaceAll("\\s", "") + "%"); //genres have been stripped of spaces in the database
            }

            for (String iString : lRemoveList)
            {
                lSelection.append(" AND ");
                lSelection.append("genres" + " NOT LIKE ?");
                lSelectionArgs.add("%" + iString.replaceAll("\\s", "") + "%"); //genres have been stripped of spaces in the database
            }

            QueryResultIterable<Manga> iFilteredManga = cupboard().withDatabase(MangaDB.getInstance().getReadableDatabase())
                                                                  .query(Manga.class)
                                                                  .withSelection(lSelection.toString(), lSelectionArgs
                                                                          .toArray(new String[lSelectionArgs
                                                                                  .size()]))
                                                                  .query();

            if (iFilteredManga.iterator().hasNext())
            {
                Intent lIntent = new Intent();
                lIntent.putParcelableArrayListExtra("MANGA", new ArrayList<>(iFilteredManga
                                                                                     .list())); //TODO decide if i want to pass whole list or query contents
                FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, lIntent);
                getDialog().dismiss();
            }
            else
            {
                Toast.makeText(getContext(), "Search result empty", Toast.LENGTH_SHORT).show();
            }

            MangaLogger.logInfo(TAG, lMethod, "Finished filter search");

        }

        //
    }
}
