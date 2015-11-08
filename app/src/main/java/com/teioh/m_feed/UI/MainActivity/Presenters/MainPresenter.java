package com.teioh.m_feed.UI.MainActivity.Presenters;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface MainPresenter{

    void initialize();

    void parseLogin();

    void onLogout();

    void busProviderRegister();

    void busProviderUnregister();

    void updateQueryChange(String newTest);

    void ButterKnifeUnbind();


}
