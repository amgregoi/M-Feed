package com.teioh.m_feed.UI.MainActivity.View.Fragments;

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
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class FollowedFragment extends Fragment implements FollowFragmentMapper {
    public final static String TAG = FollowedFragment.class.getSimpleName();

    @Bind(R.id.library_list_view) GridView mGridView;

    private FollowedPresenter mFollowedPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2_followed_fragment, container, false);
        ButterKnife.bind(this, v);

        mFollowedPresenter = new FollowedPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mFollowedPresenter.onRestoreState(savedInstanceState);
        }

        mFollowedPresenter.init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFollowedPresenter.onSaveState(outState);
    }

    @OnItemClick(R.id.library_list_view)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mFollowedPresenter.onItemClick(item.getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        mFollowedPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFollowedPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFollowedPresenter.onDestroyView();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFollowedPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setTextFilterEnabled(true);
            mGridView.setAdapter(adapter);
            registerForContextMenu(mGridView);
        }
    }

    @Override
    public void updateSource() {
        mFollowedPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mFollowedPresenter.onFilterSelected(filter);
    }
}