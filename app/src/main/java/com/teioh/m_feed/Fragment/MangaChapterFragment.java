package com.teioh.m_feed.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.teioh.m_feed.MangaJoy;
import com.teioh.m_feed.Pojo.Chapter;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MangaChapterFragment extends Fragment {

    @Bind(R.id.mangaChapterList) ListView mChapterListView;
    private ArrayList<Chapter> chapList;
    private ArrayAdapter<Chapter> mAdapter;
    private Observable<List<Chapter>> temp;
    private Manga item;
    private MangaJoy mj = new MangaJoy();
    private static int retry = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_chapters_fragment, container, false);
        ButterKnife.bind(this, v);
        item = getArguments().getParcelable("Manga");
        temp = mj.pullChaptersFromWebsite(item.getMangaURL())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<Chapter>>() {
                    @Override
                    public List<Chapter> call(Throwable throwable) {
                        Log.e("throwable", throwable.toString());
                        return null;
                    }
                });
        temp.subscribe(chapters -> udpateChapterList(chapters));
        return v;
    }

    private void udpateChapterList(List<Chapter> chapters) {
        try {
            if (chapters != null) {
                chapList = new ArrayList<>(chapters);
                mAdapter = new ArrayAdapter<>(getContext(), R.layout.chapter_list, R.id.chapter_list_item, chapList);
                mChapterListView.setAdapter(mAdapter);
                //LinearLayout header = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.manga_info_fragment, null);
                mAdapter.notifyDataSetChanged();
                retry = 0;
            } else {
                if (retry > 3) { //allow 3 attempts before we stop
                    retry = 0;
                    return;
                }
                temp.subscribe(chapters2 -> udpateChapterList(chapters2));
                retry++;
            }
        }catch(NullPointerException e)
        {
            Log.e("ChapterList", e.toString() + "\twhile updating chapter list");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
    }

}
