package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.MainActivityMap;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LoginFragment;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.R;

import butterknife.ButterKnife;

public class MainPresenterImpl implements MainPresenter {

    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private CharSequence Titles[] = {"Recent", "Library", "All"};
    private int Numbtabs = 3;

    private BaseContextMap mBaseContext;
    private MainActivityMap mMainMapper;

    public MainPresenterImpl(MainActivityMap main, BaseContextMap base) {
        mBaseContext =  base;
        mMainMapper = main;
    }

    @Override
    public void initialize() {
        mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mBaseContext.getContext()).getSupportFragmentManager(), Titles, Numbtabs);
        mMainMapper.setupTabLayout();
        mMainMapper.registerAdapter(mViewPagerAdapterMain);
        mMainMapper.setupSearchview();
        ((FragmentActivity) mBaseContext.getContext()).setTitle(mBaseContext.getContext().getString(R.string.app_name));
    }

    @Override
    public void parseLogin() {
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Fragment fragment = new LoginFragment();
            ((FragmentActivity) mBaseContext.getContext()).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                Fragment fragment = new LoginFragment();
                ((FragmentActivity) mBaseContext).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onLogout() {
        ParseUser.getCurrentUser().logOut();
        Fragment fragment = new LoginFragment();
        ((FragmentActivity) mBaseContext.getContext()).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();

    }

    @Override
    public void busProviderRegister() {
        BusProvider.getInstance().register(mMainMapper);
    }

    @Override
    public void busProviderUnregister() {
        BusProvider.getInstance().unregister(mMainMapper);

    }

    @Override
    public void updateQueryChange(String newTest) {
        BusProvider.getInstance().post(new QueryChange(newTest));

    }

    @Override
    public void ButterKnifeUnbind() {
        ButterKnife.unbind(mMainMapper);

    }
}
