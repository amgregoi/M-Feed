package com.teioh.m_feed.UI.SearchActivity.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.SearchActivity.View.Widgets.HeaderGridView;
import com.teioh.m_feed.UI.SearchActivity.View.Mappers.SearchActivityMap;
import com.teioh.m_feed.UI.SearchActivity.Presenters.SearchPresenter;
import com.teioh.m_feed.UI.SearchActivity.Presenters.SearchPresenterImpl;
import com.teioh.m_feed.UI.SearchActivity.Adapter.GenreListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements SearchActivityMap {
    public final static String TAG = SearchActivity.class.getSimpleName();

    @Bind(R.id.genreList) HeaderGridView mGenreGridView;

    private SearchPresenter mSearchPresenter;
    private Button mSearchButton;
    private RadioButton mAllButton;
    private RadioButton mOnGoingButton;
    private RadioButton mCompleteButton;

    public static Intent getnewInstance(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_fragment);
        ButterKnife.bind(this);
        mSearchPresenter = new SearchPresenterImpl(this);

        if (savedInstanceState != null) {
            mSearchPresenter.onRestoreState(savedInstanceState);
        }

        mSearchPresenter.init(getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSearchPresenter.onSavedState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSearchPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mSearchPresenter.onDestroy();
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        View header = getLayoutInflater().inflate(R.layout.search_gridview_header, null);
        mSearchButton = (Button) header.findViewById(R.id.genre_search_button);

        mGenreGridView.addHeaderView(header);
        mGenreGridView.setAdapter(adapter);
        mGenreGridView.setOnItemClickListener((parent, view, position, id) -> ((GenreListAdapter) adapter).updateItem(position - 3, view));
        mSearchButton.setOnClickListener(v -> mSearchPresenter.performSearch());
    }

    @Override
    public Context getContext() {
        return this;
    }
}
