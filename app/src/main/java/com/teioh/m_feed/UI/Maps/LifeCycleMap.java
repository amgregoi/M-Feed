package com.teioh.m_feed.UI.Maps;

import android.os.Bundle;

public interface LifeCycleMap {
    void init(Bundle bundle);

    void onSaveState(Bundle save);

    void onRestoreState(Bundle restore);

    void onPause();

    void onResume();

    void onDestroy();
}
