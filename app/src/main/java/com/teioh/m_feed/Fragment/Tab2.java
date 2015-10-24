package com.teioh.m_feed.Fragment;

import android.os.AsyncTask;
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
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener {

    @Bind(R.id.search_view_2)
    SearchView mSearchView;
    @Bind(R.id.library_list_view)
    ListView mListView;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private parseGetLibrary rdt;
    private int recentIndexUsed;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        //Initialize variables
        list = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        ButterKnife.bind(this, v);

        //setup
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
        rdt = new parseGetLibrary();
        rdt.execute();
        registerForContextMenu(mListView);
        return v;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
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

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        int menuItemIndex = menuItem.getItemId();
        switch (menuItemIndex) {
            case 0:
                ParseInstallation pi = ParseInstallation.getCurrentInstallation();
                final Manga item = list.get(recentIndexUsed);
                //TODO - fix this, not removing channel
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

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Subscribe
    public void onMangaAdded(Manga manga) {
        Log.i("FOLLOW MANGA", manga.getTitle());
        for (Manga m : list) {
            if (m.getMangaId().equals(manga.getMangaId())) {
                return;
            }
        }
        list.add(manga);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onMangaAdded(RemoveFromLibrary rm) {
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
                        manga.setPicUrl((String) obj.get("MangaPic"));
                        list.add(manga);
                    }
                }
            } catch (Exception e) {
                Log.e("Error", "Message: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}