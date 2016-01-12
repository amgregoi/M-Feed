package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

public interface ChapterPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void getImageUrls();

    void onPause();

    void onDestroyView();

    void toggleToolbar();
}
