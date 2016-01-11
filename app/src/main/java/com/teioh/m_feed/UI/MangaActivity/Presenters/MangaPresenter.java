package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

public interface MangaPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init(Intent intent);

    void onResume();

    void onPause();

    void onDestroy();

    void chapterOrderButtonClick();
}
