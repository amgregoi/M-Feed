package com.teioh.m_feed.UI.Maps;

import android.os.Bundle;

public interface LifeCycleMap {
    void init(Bundle aBundle);

    void onSaveState(Bundle aSave);

    void onRestoreState(Bundle aRestore);

    void onPause();

    void onResume();

    void onDestroy();
}
