package com.teioh.m_feed.Fragment;

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
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Pojo.RemoveFromLibrary;
import com.teioh.m_feed.Pojo.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.schedulers.Schedulers;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener {

    @Bind(R.id.search_view_2) SearchView mSearchView;
    @Bind(R.id.library_list_view) ListView mListView;
    private ArrayList<Manga> list;
    private ArrayList<Manga> temp;
    private SearchableAdapter mAdapter;
    private int recentIndexUsed;


    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Manga Feed");
        list = new ArrayList<>();
        temp = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
        registerForContextMenu(mListView);

        return v;
    }

    @OnItemClick(R.id.library_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Bundle b = new Bundle();
        b.putParcelable("Manga", item);
        Fragment fragment = new MangaFragment();
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.library_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            recentIndexUsed = info.position;
            menu.setHeaderTitle(list.get(recentIndexUsed).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.library_item_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    // TODO - needs to send object update to other tabs and update db as well
    @Override public boolean onContextItemSelected(MenuItem menuItem) {
        int menuItemIndex = menuItem.getItemId();
        switch (menuItemIndex) {
            case 0:
                ParseInstallation pi = ParseInstallation.getCurrentInstallation();
                final Manga item = list.get(recentIndexUsed);
                ArrayList<String> channel = new ArrayList<>(Arrays.asList("m_" + item.getMangaId()));
                ParseInstallation.getCurrentInstallation().removeAll("channels", channel);
                pi.saveEventually(
                        new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    BusProvider.getInstance().post(new RemoveFromLibrary(item));
                                } else {
                                    Log.e("ParseException: fail,  ", e.toString());
                                }
                            }
                        });
                break;
            case 1:
                Log.e("cancel", "i messed up");
                break;
            default:
                break;
        }

        return true;
    }

    @Override public void onResume() {
        super.onResume();
        list.clear();
        temp.clear();
        BusProvider.getInstance().register(this);
        parseGetFollowedLibrary();
        Log.e("TAB2", "DERP");
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Querychange for searchview
    @Override public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    // Text submit returns false, because we update dynamically while query changes in the above method <onQueryTextChange()>
    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //Event method, when a manga is followed, we want to add it to our library
    @Subscribe public void onMangaAdded(Manga manga) {
        Log.i("FOLLOW MANGA", manga.getTitle());
        for (Manga m : list) {
            if (m.getMangaId().equals(manga.getMangaId())) {
                return;
            }
        }
        list.add(manga);
        Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
    }

    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        Log.i("UNFOLLOW MANGA", manga.getTitle());
        for (Manga m : list) {
            if (m.getMangaId().equals(manga.getMangaId())) {
                list.remove(m);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO

    }

    // querys parse for manga we follow and populates our list
    private void parseGetFollowedLibrary() {
        Observable.just("").subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        for (Manga m : temp)
                            list.add(m);
                        Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
                        mAdapter.notifyDataSetChanged();
                    });
                })
                .subscribe(s -> {
                    try {
                        List<String> channels = ParseInstallation.getCurrentInstallation().getList("channels");
                        for (int i = 0; i < channels.size(); i++) {
                            String mangaId = channels.get(i).replace("m_", "");

                            ParseQuery<ParseObject> query = new ParseQuery<>("Manga");
                            query.whereEqualTo("objectId", mangaId);
                            List<ParseObject> p = query.find();

                            if (p != null) {
                                ParseObject obj = p.get(0);
                                Manga manga = new Manga();
                                manga.setTitle((String) obj.get("MangaTitle"));
                                manga.setMangaUrl((String) obj.get("MangaUrl"));
                                manga.setPicUrl((String) obj.get("MangaPic"));
                                manga.setLatestChapter((String) obj.get("LatestChapter"));
                                manga.setMangaId(obj.getObjectId());
                                manga.setLastUpdate(obj.getUpdatedAt());
                                if (getActivity() == null)
                                    return;       // if app closes, and we lose the activity we stop the async task
                                temp.add(manga);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Message: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

}