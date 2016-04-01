package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecyclerSearchAdapater;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class FollowedFragment extends Fragment implements FollowFragmentMapper {
    public final static String TAG = FollowedFragment.class.getSimpleName();

    RecyclerView mGridView;

    private FollowedPresenter mFollowedPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2_followed_fragment, container, false);
//        ButterKnife.bind(this, v);
        mGridView = (RecyclerView) v.findViewById(R.id.library_list_view);

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
        ButterKnife.unbind(this);
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
//            mGridView.setAdapter(adapter);
//            registerForContextMenu(mGridView);
        }
    }

    @Override
    public void onGenreFilterSelected(ArrayList<String> keep, ArrayList<String> remove) {
        mFollowedPresenter.onGenreFilterSelected(keep, remove);
    }

    @Override
    public void onClearGenreFilter() {
        mFollowedPresenter.onClearGenreFilter();
    }

    @Override
    public void registerAdapter(RecyclerSearchAdapater mAdapter, RecyclerView.LayoutManager layout) {
        if(mAdapter != null){
            mGridView.setAdapter(mAdapter);
            mGridView.setLayoutManager(layout);
            mGridView.addItemDecoration(new RecyclerSearchAdapater.SpacesItemDecoration(8));
        }
    }

    @Override
    public void updateSelection(Manga manga) {
        mFollowedPresenter.updateSelection(manga);
    }

    @Override
    public void updateSource() {
        mFollowedPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mFollowedPresenter.onFilterSelected(filter);
    }

    private FollowedFragmentListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (context instanceof FollowedFragmentListener) listener = (FollowedFragmentListener) context;
        else throw new ClassCastException(context.toString() + " must implement LibraryFragment.RecentFragmentListener");
    }

    public interface FollowedFragmentListener {
        void updateRecentSelection(Long id);
        void refreshRecentSelection();

    }

    @Override
    public void updateRecentSelection(Long id){
        listener.updateRecentSelection(id);
    }

    @Override
    public void refreshRecentSelection() {
        listener.refreshRecentSelection();
    }

}