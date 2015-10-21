package com.teioh.m_feed.Fragment;

import android.os.AsyncTask;
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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.UpdateListEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Tab3 extends Fragment implements SearchView.OnQueryTextListener {
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private List<ParseObject> ob;
    private parseLibrary p1, p2, p3, p4;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        //Initialize variables
        list = new ArrayList<>();
        mSearchView = (SearchView) v.findViewById(R.id.search_view);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setFastScrollEnabled(true);
        mListView.setVisibility(View.GONE);
        setup();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final Manga item = (Manga) adapter.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putParcelable("Manga", item);
                MangaFragment fragment = new MangaFragment();
                fragment.setArguments(b);
                getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
            }
        });
        return v;
    }


    /* parseLibrary AsyncTask
     * queries manga library*/
    private class parseLibrary extends AsyncTask<Character, Character, Void> {

        @Override
        protected Void doInBackground(Character... params) {

            // Create the array
            try {
                int skip = 0, querySize = 250;
                char section = params[0];
                char end = params[1];


                do {
                    ParseQuery<ParseObject> query = new ParseQuery<>("Manga");
                    query.orderByAscending("MangaTitle");
                    query.whereEqualTo("Section", Character.toString((section)));
                    query.setLimit(querySize);
                    query.setSkip(skip);
                    ob = query.find();

                    for (ParseObject manga : ob) {
                        Manga m = new Manga();
                        String date = (String) manga.get("DateUpdated");
                        Date d = new Date();
                        if (date.equals("No recent updates")) {
                            d.setDate(0);
                            d.setMonth(0);
                            d.setYear(0);
                        } else {
                            String[] dateArr = date.split("/");
                            d.setDate(Integer.parseInt(dateArr[0]));
                            d.setMonth(Integer.parseInt(dateArr[1]));
                            d.setYear(Integer.parseInt(dateArr[2]));
                        }
                        m.setLastUpdate(d);
                        m.setTitle((String) manga.get("MangaTitle"));
                        m.setMangaUrl((String) manga.get("MangaUrl"));
                        m.setMangaId(manga.getObjectId());
                        m.setPicUrl((String) manga.get("MangaPic"));
                        list.add(m);
                    }
                    if (ob.size() < querySize) {
                        skip = 0;
                        if (section == '0') {
                            section = 'A';
                        } else {
                            section++;
                        }

                    } else {
                        skip += querySize;
                    }
                } while (!(section == end + 1));
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                //tells me which AsyncTask is failing by the starting character section
                Log.e("Section", Character.toString(params[0]));
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setup() {
        //ListView Setup
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        //AsyncTask Setup
        p1 = new parseLibrary();
        p2 = new parseLibrary();
        p3 = new parseLibrary();
        p4 = new parseLibrary();

        //Populates the listview
        p1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 'A', 'A');  //for testing
        //p1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, '0', 'M');
        //p3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 'N', 'T');
        //p4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 'U', 'Z');

        //sorts listview after populated
        new checkAsyncComplete().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Search view setup
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /*
     * Waits for the 3 Async tasks to complete
     * Sorts the List, updates the view
     */
    private class checkAsyncComplete extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (true) {

                if (p1.getStatus() != AsyncTask.Status.RUNNING && p3.getStatus() != AsyncTask.Status.RUNNING
                        && p4.getStatus() != AsyncTask.Status.RUNNING) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(list, new Comparator<Manga>() {
                                public int compare(Manga emp1, Manga emp2) {
                                    return emp1.getTitle().compareToIgnoreCase(emp2.getTitle());
                                }
                            });

                            mListView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    return null;
                }
            }

        }

    }


    @Override
    public void onStop() {
        super.onStop();
        p1.cancel(true);
        p3.cancel(true);
        p4.cancel(true);
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
    }

    @Subscribe
    public void onUpdateListEvent(UpdateListEvent event) {
        //update list
        //TODO - makes it here
        //need to update list
        //copy this method to update other two tabs
        Log.e("ARE YOU WORKING", "YES I AM");
    }

}