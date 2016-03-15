package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;



public interface ReaderPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init(Intent intent);

    void onPause();

    void onResume();

    void onDestroy();

    void updateToolbar(int position);

    void incrementChapterPage(int position);

    void decrementChapterPage(int position);

    void updateChapterViewStatus(int position);

    void onRefreshButton(int position);
}
