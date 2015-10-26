package com.teioh.m_feed.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Pojo.RemoveFromLibrary;
import com.teioh.m_feed.Pojo.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.schedulers.Schedulers;

public class Tab1 extends Fragment {

    @Bind(R.id.recent_list_view) ListView mListView;
    private ArrayList<Manga> list;
    private ArrayList<Manga> temp;
    private SearchableAdapter mAdapter;
    private final int recent_limit = 10;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Manga Feed");
        list = new ArrayList<>();
        temp = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        return v;
    }

    @OnItemClick(R.id.recent_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Bundle b = new Bundle();
        b.putParcelable("Manga", item);
        Fragment fragment = new MangaFragment();
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        parseGetRecentUpdates();
        Log.e("TAB1", "DERP");
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Event method
    // Do not currently do anything with this method, we do not want to arbitrarily add to the recent updates list
    @Subscribe public void onMangaAdded(Manga manga) {
        return;
    }

    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        Log.i("UNFOLLOW MANGA", manga.getTitle());
        for (Manga m : list) {
            if (m.getMangaId().equals(manga.getMangaId())) {
                temp.remove(m);
                list.remove(m);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO
        Log.e("herpa", "Derpa");
    }

    // querys parse for manga we follow and populates with most recently updated
    private void parseGetRecentUpdates() {
        Observable.just("").subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            for (Manga m : temp)
                                list.add(m);
                            mAdapter.notifyDataSetChanged();

                        });
                })
                .subscribe(s -> {
                    try {
                        list.clear();
                        temp.clear();
                        List<String> channels = ParseInstallation.getCurrentInstallation().getList("channels");
                        //removes prefix "m_"
                        for (int i = 0; i < channels.size(); i++) {
                            String newId = channels.get(i).replace("m_", "");
                            channels.set(i, newId);
                        }
                        ParseQuery<ParseObject> query = new ParseQuery<>("Manga");
                        query.orderByDescending("updatedAt");
                        query.whereContainedIn("objectId", channels);
                        query.setLimit(recent_limit);

                        List<ParseObject> ob = query.find();
                        for (ParseObject obj : ob) {
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
                    } catch (Exception e) {
                        Log.e("Error", "Message: " + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.e("temp count", Integer.toString(temp.size()));
                });
    }


}
