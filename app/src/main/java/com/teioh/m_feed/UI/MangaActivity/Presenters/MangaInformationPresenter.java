package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface MangaInformationPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init(Bundle bundle);

    void onFollwButtonClick();

    void setFollowButtonText(boolean follow, boolean notInit);

    void onResume();

    void onPause();

    void onDestroyView();
}
