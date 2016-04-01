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
import com.teioh.m_feed.UI.MainActivity.Presenters.LibraryPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.LibraryPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.LibraryFragmentMapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class LibraryFragment extends Fragment implements LibraryFragmentMapper {
    public final static String TAG = LibraryFragment.class.getSimpleName();

    RecyclerView mGridView;

    private LibraryPresenter mLibraryPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3_library_fragment, container, false);
        mGridView = (RecyclerView) v.findViewById(R.id.all_list_view);
//        ButterKnife.bind(this, v);


        mLibraryPresenter = new LibraryPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLibraryPresenter.onRestoreState(savedInstanceState);
        }
        mLibraryPresenter.init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLibraryPresenter.onSaveState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLibraryPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLibraryPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLibraryPresenter.onDestroyView();
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
//            mGridView.setFastScrollEnabled(true);
//            mGridView.setAdapter(adapter);
//            mGridView.setTextFilterEnabled(true);
        }
    }

    @Override
    public void onGenreFilterSelected(ArrayList<String> keep, ArrayList<String> remove) {
        mLibraryPresenter.onGenreFilterSelected(keep, remove);
    }

    @Override
    public void onClearGenreFilter() {
        mLibraryPresenter.onClearGenreFilter();
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
        mLibraryPresenter.updateSelection(manga);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mLibraryPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void updateSource() {
        mLibraryPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mLibraryPresenter.onFilterSelected(filter);
    }

    private LibraryFragmentListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (context instanceof LibraryFragmentListener) listener = (LibraryFragmentListener) context;
        else throw new ClassCastException(context.toString() + " must implement LibraryFragment.RecentFragmentListener");
    }

    public interface LibraryFragmentListener {
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