package com.teioh.m_feed.UI.SearchActivity.View.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.SearchActivity.View.Mappers.SearchResultMap;
import com.teioh.m_feed.UI.SearchActivity.Presenters.SearchResultPresenter;
import com.teioh.m_feed.UI.SearchActivity.Presenters.SearchResultPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultFragment extends Fragment implements SearchResultMap {
    public final static String TAG = SearchResultFragment.class.getSimpleName();

    @Bind(R.id.all_list_view) GridView mGridView;

    private SearchResultPresenter mSearchResultPresenter;


    public static Fragment getNewInstance(List<Manga> manga) {
        Fragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SearchResultPresenterImpl.MANGA_LIST_KEY, new ArrayList<>(manga));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3_library_fragment, container, false);
        ButterKnife.bind(this, v);


        mSearchResultPresenter = new SearchResultPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchResultPresenter.onRestoreState(savedInstanceState);
        }
        mSearchResultPresenter.init(getArguments());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSearchResultPresenter.onSavedState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchResultPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchResultPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSearchResultPresenter.onDestroy();
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setFastScrollEnabled(true);
            mGridView.setAdapter(adapter);
            mGridView.setTextFilterEnabled(true);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                    Manga item = (Manga) adapter.getItemAtPosition(position);
                    mSearchResultPresenter.onItemClick(item.getTitle());
                }
            });
        }
    }

}