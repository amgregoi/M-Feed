package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by amgregoi on 1/11/16.
 */
public interface ReaderPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init(Intent intent);

    void onPause();

    void onResume();

    void onDestroy();
}
