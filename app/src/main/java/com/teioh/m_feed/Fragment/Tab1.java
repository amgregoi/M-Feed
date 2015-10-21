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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by hp1 on 21-01-2015.
 * RECENT
 * keep last several updates
 */
public class Tab1 extends Fragment {

    private ListView mListView;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private parseGetLibrary rdt;
    private int recent_limit;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.tab1, container, false);
        recent_limit = 10;
        list = new ArrayList<>();
        mListView = (ListView) v.findViewById(R.id.recent_list_view);
        mAdapter = new SearchableAdapter(getContext(), list);

        //setup
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        rdt = new parseGetLibrary();
        rdt.execute();

        return v;
    }

    private class parseGetLibrary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<String> channels = ParseInstallation.getCurrentInstallation().getList("channels");
                int recentCount = 0;
                for (int i = 0; i < channels.size(); i++) {
                    String mangaId = channels.get(i).replace("m_", "");

                    ParseQuery<ParseObject> query = new ParseQuery<>("Manga");
                    query.whereEqualTo("objectId", mangaId);
                    List<ParseObject> p = query.find();
                    if (p != null && recentCount <= recent_limit) {
                        ParseObject obj = p.get(0);
                        Manga manga = new Manga();
                        String date = (String) obj.get("DateUpdated");
                        if (date.equals("No recent updates")) {
                            continue;
                        }
                        String[] dateArr = date.split("/");
                        manga.setTitle((String) obj.get("MangaTitle"));
                        Date d = new Date();
                        d.setDate(Integer.parseInt(dateArr[0]));
                        d.setMonth(Integer.parseInt(dateArr[1]));
                        d.setYear(Integer.parseInt(dateArr[2]));
                        manga.setLastUpdate(d);
                        manga.setMangaUrl((String) obj.get("MangaUrl"));
                        manga.setMangaId(obj.getObjectId());
                        list.add(manga);
                        recentCount++;
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(list, new Comparator<Manga>() {
                            public int compare(Manga emp1, Manga emp2) {
                                // -1 inverts the list, making the most updated on top
                                return -1 * emp1.getLastUpdated().compareTo(emp2.getLastUpdated());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

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
        //should probably find a new way to populate recent list
        //TODO
    }

}
