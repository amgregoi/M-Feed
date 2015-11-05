package com.teioh.m_feed.MainPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.MangaPackage.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.OttoBus.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener {

    @Bind(R.id.search_view_2) SearchView mSearchView;
    @Bind(R.id.library_list_view) GridView mListView;
    private Observable<List<Manga>> observableMangaList;
    private ArrayList<Manga> libraryList;
    private SearchableAdapter mAdapter;
    private int recentIndexUsed;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        ButterKnife.bind(this, v);
        MangaFeedDbHelper.getInstance().createDatabase();


        libraryList = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), libraryList);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        registerForContextMenu(mListView);

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);


        return v;
    }

    @OnItemClick(R.id.library_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        startActivity(intent);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.library_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            recentIndexUsed = info.position;
            menu.setHeaderTitle(libraryList.get(recentIndexUsed).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.library_item_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override public boolean onContextItemSelected(MenuItem menuItem) {
        int menuItemIndex = menuItem.getItemId();
        switch (menuItemIndex) {
            case 0:
                final Manga item = libraryList.get(recentIndexUsed);
                MangaFeedDbHelper.getInstance().updateMangaUnfollow(item);                 //updates database
                BusProvider.postOnMain(new RemoveFromLibrary(item)); // remove from list
                // TODO - use parse to store a users follow
                // reset database following values, and update with users list
                break;
            case 1:
                Log.e("cancel", "oops");
                break;
            default:
                break;
        }
        return true;
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        observableMangaList = ReactiveQueryManager.getFollowedMangaObservable();
        observableMangaList.subscribe(manga -> populateLibraryListView(manga));
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // Otto Event Bus
    //Event method, when a manga is followed, we want to add it to our library
    @Subscribe public void onMangaAdded(Manga manga) {
        if(!libraryList.contains(manga)) {
            libraryList.add(manga);
            Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter.notifyDataSetChanged();
            Log.i("FOLLOW MANGA success ", manga.getTitle());
        }

    }

    // Otto Event Bus
    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        if(libraryList.contains(manga)) {
            libraryList.remove(manga);
            mAdapter.notifyDataSetChanged();
            Log.i("UNFOLLOW MANGA success", manga.getTitle());
        }
    }

    // Otto Event Bus
    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO
    }

    //finishes async task for updating followed library
    private void populateLibraryListView(List<Manga> mangaList){
        libraryList.clear();
        for (Manga m : mangaList) {
            if(!libraryList.contains(m)) {
                libraryList.add(m);
            }
        }
        Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
    }
}