package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class FilterDialogFragment extends DialogFragment {
    public final static String TAG = FilterDialogFragment.class.getSimpleName();

    @Bind(R.id.genreList) GridView mGenreGridView;
    @Bind(R.id.genre_search_button) Button mSearchButton;
    @Bind(R.id.genre_cancel_button) Button mCancelButton;
    @Bind(R.id.genre_clear_button) Button mClearButton;

    private GenreListAdapter mAdapter;

    public static DialogFragment getnewInstance(){
        DialogFragment dialog = new FilterDialogFragment();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, v);

        mAdapter = new GenreListAdapter(getContext(), new ArrayList<>(Arrays.asList(MangaJoy.genres)));
        registerAdapter(mAdapter);

//        mSearchButton.setBackgroundColor(getResources().getColor(R.color.charcoal));
//        mCancelButton.setBackgroundColor(getResources().getColor(R.color.charcoal));
//        mClearButton.setBackgroundColor(getResources().getColor(R.color.charcoal));

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void registerAdapter(BaseAdapter adapter) {
        mGenreGridView.setAdapter(adapter);
        mGenreGridView.setOnItemClickListener((parent, view, position, id) -> ((GenreListAdapter) adapter).updateItem(position, view));
        mSearchButton.setOnClickListener(v -> performSearch());
        mCancelButton.setOnClickListener(v -> {
            FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_CANCELED, null);
            getDialog().dismiss();
        });
        mClearButton.setOnClickListener(v -> {
            mAdapter = new GenreListAdapter(getContext(), new ArrayList<>(Arrays.asList(MangaJoy.genres)));
            registerAdapter(mAdapter);
        });
    }

    private void performSearch() {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append("mSource" + " = ?");
        selectionArgs.add(WebSource.getCurrentSource());

        List<String> keepList = mAdapter.getGenreListByStatus(1);
        List<String> removeList = mAdapter.getGenreListByStatus(2);

        //search with no parameters selected, reset to original data
        if(keepList.size() == 0 && removeList.size() == 0) {
            FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, null);
            getDialog().dismiss();
        }
        else {
            Log.e(TAG, "starting setup");

            for (String s : keepList) {
                selection.append(" AND ");
                selection.append("mGenres" + " LIKE ?");
                selectionArgs.add("%" + s.replaceAll("\\s", "") + "%"); //genres have been stripped of spaces in the database
            }

            for (String s : removeList) {
                selection.append(" AND ");
                selection.append("mGenres" + " NOT LIKE ?");
                selectionArgs.add("%" + s.replaceAll("\\s", "") + "%"); //genres have been stripped of spaces in the database
            }

            QueryResultIterable<Manga> filteredManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                    .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                    .query();

            if (filteredManga.iterator().hasNext()) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("KEEP", new ArrayList<>(keepList));
                intent.putStringArrayListExtra("REMOVE", new ArrayList<>(removeList));
                intent.putParcelableArrayListExtra("MANGA", new ArrayList<>(filteredManga.list())); //TODO decide if i want to pass whole list or query contents
                FilterDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, intent);
                getDialog().dismiss();
            } else {
                Toast.makeText(getContext(), "Search result empty", Toast.LENGTH_SHORT).show();
            }
        }

        //
    }
}
