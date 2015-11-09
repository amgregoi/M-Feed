package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterListPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterListPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterListMapper;
import com.teioh.m_feed.R;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ChapterListFragment extends Fragment implements ChapterListMapper {

    @Bind(R.id.mangaChapterList) ListView mChapterListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefresh;

    private ChapterListPresenter mChapterListPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_chapters_fragment, container, false);
        ButterKnife.bind(this, v);

        mChapterListPresenter = new ChapterListPresenterImpl(this, getArguments());
        mChapterListPresenter.getChapterList();

        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mChapterListPresenter.butterKnifeUnbind();
    }

    @OnItemClick(R.id.mangaChapterList)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        mChapterListPresenter.onChapterClicked(pos);
    }

    @Override
    public void startRefresh() {
        swipeRefresh.setRefreshing(true);

    }

    @Override
    public void stopRefresh() {
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setEnabled(false);
    }

    @Override
    public void setupSwipeRefresh() {
        swipeRefresh.post(() -> swipeRefresh.setRefreshing(true));
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        mChapterListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
