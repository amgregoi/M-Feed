package com.teioh.m_feed.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Pojo.LibraryFinished;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Pojo.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.MangaFeedContract.MangaFeedEntry;
import com.teioh.m_feed.Utils.MangaFeedDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Tab3 extends Fragment implements SearchView.OnQueryTextListener {
    @Bind(R.id.search_view_3) SearchView mSearchView;
    @Bind(R.id.list_view) ListView mListView;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private List<ParseObject> ob;
    private int library_finish = 0;
    private MangaFeedDbHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        ButterKnife.bind(this, v);

        list = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setFastScrollEnabled(true);
        mListView.setVisibility(View.GONE);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        mDbHelper = new MangaFeedDbHelper(getContext());
        mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(), 1, 1);

        setup();

        return v;
    }

    @OnItemClick(R.id.list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Bundle b = new Bundle();
        b.putParcelable("Manga", item);
        Fragment fragment = new MangaFragment();
        fragment.setArguments(b);
        mSearchView.clearFocus();           //fixed trouble with double backpress
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
    }

    //setup listview & searhview
    private void setup() {
        Cursor c = mDbHelper.dbExists();
        if (c == null) {
            Log.i("PARSE-Db", "Pulling from parse db");
            rxParseLibraryCall();
        } else {
            Log.i("LOCAL-Db", "Pulling from local sqlite db");
            c.moveToFirst();
            Gson gson = new Gson();
            while (!c.isAfterLast()) {
                String obj = c.getString(c.getColumnIndex(MangaFeedEntry.COLUMN__NAME_OBJECT));
                Manga m = gson.fromJson(obj, Manga.class);
                list.add(m);
                c.moveToNext();
            }
            Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
        }

        //Search view setup
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(), 1, 1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onUpdateListEvent(UpdateListEvent event) {
        //update list
        //TODO - This does work (*)
        //need to update list
        //copy this method to update other two tabs
        Log.e("ARE YOU WORKING", "YES I AM");
    }

    //rx functions
    public void rxWrapParseLibrary(String... chars) {
        Observable.from(chars).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> rxParseLibraryCall());
    }

    public void rxSplitParseLibrary(String... chars) {
        Observable.from(chars)
                .subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                    getActivity().runOnUiThread(() -> {

                        BusProvider.getInstance().post(new LibraryFinished());
                    });
                })
                .subscribe(x -> rxParseLibrary(x));
    }

    public void rxBuildDatabase(String... chars) {
        Observable.from(chars).subscribeOn(Schedulers.newThread())
                .subscribe(x -> mDbHelper.addMangaToTable(list));
    }

    //rx helper functions
    private void rxParseLibrary(String s) {
        Log.e("SUCK", s + "\t" + Integer.toString(list.size()));
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
                    manga.setMangaId(obj.getObjectId());
                    manga.setLastUpdate(obj.getUpdatedAt());
                    String objID = "m_" + obj.getObjectId();
                    if (channels != null && channels.contains(objID)) {
                        manga.setFollowing(true);
                    }
                    list.add(manga);
                }
                skip += querySize;
            } while (ob.size() == querySize);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void rxUpdateList() {
//        Collections.sort(list, new Comparator<Manga>() {
//            public int compare(Manga emp1, Manga emp2) {
//                return emp1.getTitle().compareToIgnoreCase(emp2.getTitle());
//            }
//        });
        getActivity().runOnUiThread(() -> {
            Collections.sort(list, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mListView.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            rxBuildDatabase("go");
            //Log.e("count", Integer.toString(list.size()));
        });
    }

    private void rxParseLibraryCall() {
        //split the calls up to speed it up
        rxSplitParseLibrary("a", "b", "c", "d", "e");
        rxSplitParseLibrary("f", "g", "h", "i");
        rxSplitParseLibrary("j", "k", "l", "m", "n");
        rxSplitParseLibrary("o", "p", "q", "r");
        rxSplitParseLibrary("s", "t", "u", "v");
        rxSplitParseLibrary("w", "x", "y", "z", "0");
        //hello("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0");
    }

//    //populates local database if it doesn't already exist
//    private class AsyncBuildDatabase extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            mDbHelper.addMangaToTable(list);
//            return null;
//        }
//    }

    @Subscribe
    public void onLibraryFinished(LibraryFinished l) {
        library_finish++;
        if (library_finish > 5) {
            library_finish = 0;
            rxUpdateList();
            Log.e("count", Integer.toString(list.size()));
        }
    }

}