package com.teioh.m_feed.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.MangaActivity;
import com.teioh.m_feed.Utils.LibraryFinished;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Utils.RemoveFromLibrary;
import com.teioh.m_feed.Utils.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Database.MangaFeedDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class Tab3 extends Fragment implements SearchView.OnQueryTextListener {
    @Bind(R.id.search_view_3) SearchView mSearchView;
    @Bind(R.id.all_list_view) GridView mListView;
    private ArrayList<Manga> list;
    private ArrayList<Manga> temp;
    private List<ParseObject> ob;
    private SearchableAdapter mAdapter;
    private int library_finish = 0;
    MangaFeedDbHelper mDbHelper;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Manga Feed");

        list = new ArrayList<>();
        temp = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setFastScrollEnabled(true);
        mListView.setVisibility(View.GONE);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        mDbHelper = new MangaFeedDbHelper(getContext());
        mDbHelper.createDatabase();

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");

        return v;
    }

    @OnItemClick(R.id.all_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        startActivityForResult(intent, 1);

//        Bundle b = new Bundle();
//        b.putParcelable("Manga", item);
//        Fragment fragment = new MangaFragment();
//        fragment.setArguments(b);
//        mSearchView.clearFocus();           // fixed trouble with double backpress
//        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Manga manga = data.getParcelableExtra("result");
                if(manga != null) {
                    if (manga.getFollowing()) {
                        Log.e("fuck you", "im adding wut i want");
                        BusProvider.getInstance().post(manga);
                    } else {
                        BusProvider.getInstance().post(new RemoveFromLibrary(manga));
                    }
                }
            }
        }
    }



    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        if (list.size() == 0)
            populateListView();
        else
            mListView.setVisibility(View.VISIBLE);
    }

    @Override public void onPause() {
        super.onPause();
        library_finish = 0;         //reset value, if view is paused mid call
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe public void onMangaAdded(Manga manga) {
        return;
    }

    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();

        for(Manga m: list)
        {
            if(m.getTitle().equals(manga.getTitle()))
                m.setFollowing(manga.getFollowing());
        }
    }

    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        // TODO
        // pull recently updated mangas from parse
        // update local storage with latest chapter
        // update objects currently in listview
    }

    //querychange for searchview
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    //text submit returns false, because we update dynamically while query changes in the above method <onQueryTextChange()>
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    // populating listview on mainthread was causing a momentary freeze when reopening app
    // so it is now done off the ui thread
    public void populateListView() {
        Observable.just("").subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                            if (getActivity() == null) {
                                Log.e("populateListView", "activity gone");
                                return;
                            }
                            getActivity().runOnUiThread(() -> {
                                for (Manga m : temp) {
                                    list.add(m);
                                }
                                Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
                                mListView.setVisibility(View.VISIBLE);
                                mAdapter.notifyDataSetChanged();
                            });

                        }
                )
                .subscribe(x -> {
                        // Iterate books
                        QueryResultIterable<Manga> itr = cupboard().withDatabase(mDbHelper.getReadableDatabase()).query(Manga.class).query();
                        for (Manga manga : itr) {
                            temp.add(manga);
                        }
                        itr.close();
                    Log.e("List Size", Integer.toString(temp.size()));
                });
    }

    // function that pulls the parse database to be stored locally
    private void rxParseLibrary(String s) {
        Log.e("Section", s + "\t" + Integer.toString(temp.size()));
        try {
            int skip = 0, querySize = 1000;
            List<String> channels = ParseInstallation.getCurrentInstallation().getList("channels");
            do {
                ParseQuery<ParseObject> query = new ParseQuery<>("Manga");
                query.orderByAscending("MangaTitle");
                query.whereEqualTo("Section", s.toUpperCase());
                query.setLimit(querySize);
                query.setSkip(skip);
                ob = query.find();
                for (ParseObject obj : ob) {
                    Manga manga = new Manga();
                    manga.setTitle((String) obj.get("MangaTitle"));
                    manga.setMangaUrl((String) obj.get("MangaUrl"));
                    manga.setPicUrl((String) obj.get("MangaPic"));
                    manga.setLatestChapter((String) obj.get("LatestChapter"));
                    manga.setMangaId(obj.getObjectId());
                    if (manga.getMangaId() == null) {
                        Log.e("manga", manga.getTitle());
                        System.exit(-1);
                    }
                    manga.setLastUpdate(obj.getUpdatedAt());
                    String objID = "m_" + obj.getObjectId();
                    if (channels != null && channels.contains(objID)) {
                        manga.setFollowing(true);
                    }
                    if (getActivity() == null)
                        return;       // if app closes, and we lose the activity we stop the async task
                    temp.add(manga);
                }
                skip += querySize;
            } while (ob.size() == querySize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // updates listview calls build database after we finish pulling from parse
    private void rxUpdateList() {

        getActivity().runOnUiThread(() -> {
            list.clear();
            for (Manga m : temp) {
                list.add(m);
                cupboard().withDatabase(mDbHelper.getWritableDatabase()).put(m);
            }
            Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mListView.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            //rxBuildDatabase();
        });
    }

    // calls the function that pulls in each section of the database
    // there are 6 groups total, each time this is called, it is run on a new thread
    public void rxSplitParseLibrary(String... chars) {
        Observable.from(chars)
                .subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                    if (getActivity() == null) {
                        Log.e("rxSplitParseLibrary", "activity gone");
                        return;
                    }
                    getActivity().runOnUiThread(() -> {
                        BusProvider.getInstance().post(new LibraryFinished());
                    });
                }).doOnError(throwable -> throwable.printStackTrace())
                .subscribe(x -> rxParseLibrary(x));
    }

    // function that calls the 6 groups that split the library into sections
    private void rxParseLibraryCall() {
        // split the calls up to speed it up
//        rxSplitParseLibrary("a", "b", "c", "d", "e");
//        rxSplitParseLibrary("f", "g", "h", "i");
//        rxSplitParseLibrary("j", "k", "l", "m", "n");
//        rxSplitParseLibrary("o", "p", "q", "r");
//        rxSplitParseLibrary("s", "t", "u", "v");
//        rxSplitParseLibrary("w", "x", "y", "z", "0");
//        rxSplitParseLibrary("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0");
    }

}