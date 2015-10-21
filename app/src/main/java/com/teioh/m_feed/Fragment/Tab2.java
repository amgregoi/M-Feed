package com.teioh.m_feed.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.UpdateListEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener {

    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private parseGetLibrary rdt;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);

        //Initialize variables
        list = new ArrayList<>();
        mSearchView = (SearchView) v.findViewById(R.id.search_view);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mAdapter = new SearchableAdapter(getContext(), list);

        //setup
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
        rdt = new parseGetLibrary();
        rdt.execute();


        return v;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // RemoteDataTask AsyncTask
    private class parseGetLibrary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
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
                        String date = (String) obj.get("DateUpdated");
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
                        manga.setTitle((String) obj.get("MangaTitle"));
                        manga.setLastUpdate(d);
                        manga.setMangaUrl((String) obj.get("MangaUrl"));
                        manga.setMangaId(obj.getObjectId());
                        list.add(manga);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

    }


    @Subscribe
    public void onMangaAdded(Manga manga) {
        list.add(manga);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onUpdateListEvent(UpdateListEvent event) {
        //update list
        //TODO
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

    @Override
    public void onStop() {
        super.onStop();
        if (rdt.getStatus() == AsyncTask.Status.RUNNING) {
            rdt.cancel(true);
        }
    }
}