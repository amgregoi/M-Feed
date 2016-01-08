package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import java.util.List;

public interface ChapterReaderPresenter {

    void initialize();

    void getImageUrls();

    void updateView(List<String> urlList);

    void onDestroyView();

    void updateOffsetCounter(int offset, int position);

    void updateState(int state);

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

}
