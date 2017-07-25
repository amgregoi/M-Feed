package com.teioh.m_feed.UI.ReaderActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterMangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterNovelPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureTextView;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterNovelFragment extends Fragment implements IReader.NovelFragmentView, GestureViewPager.UserGestureListener
{
    public final static String TAG = ChapterMangaFragment.class.getSimpleName();


    @Bind(R.id.novel_text_content) GestureTextView mTextView;

    private IReader.NovelFragmentPresenter mChapterPresenter;
    private Listeners.ReaderListener mListener;


    /***
     * This function creates and returns a new instance of the fragment.
     *
     * @return
     */
    public static Fragment getNewInstance(boolean aFollowing, Chapter aChapter, int aPosition)
    {
        //TODO.. update
        Bundle lBundle = new Bundle();
        lBundle.putBoolean(ChapterMangaPresenter.CHAPTER_PARENT_FOLLOWING, aFollowing);
        lBundle.putParcelable(Chapter.TAG + ":" + aPosition, aChapter);
        lBundle.putInt(ChapterMangaPresenter.CHAPTER_POSITION_LIST_PARCELABLE_KEY, aPosition);

        Fragment lFragment = new ChapterNovelFragment();
        lFragment.setArguments(lBundle);
        return lFragment;
    }

    /***
     * This function handles single taps on images in the viewpager.
     */
    @Override
    public void onSingleTap()
    {
        mChapterPresenter.toggleToolbar();
    }

    @Override
    public void onLeft()
    {
        decrementChapter();
    }

    @Override
    public void onRight()
    {
        incrementChapter();
    }


    @Override
    public void setUserGestureListener()
    {
        //left blank
    }

    /***
     * this function updates the chapter view status.
     */
    @Override
    public void updateChapterViewStatus()
    {
        if (mChapterPresenter != null)
        {
            mChapterPresenter.updateChapterViewStatus();
        }
    }

    /***
     * This function increments the chapter.
     */
    @Override
    public void incrementChapter()
    {
        mListener.incrementChapter();
    }

    /***
     * This function decrements the chapter.
     */
    @Override
    public void decrementChapter()
    {
        mListener.decrementChapter();
    }

    /***
     * This function shows the header and footer tool bars.
     */
    @Override
    public void toggleToolbar()
    {
        mListener.toggleToolbar();
    }

    @Override
    public void startToolbarTimer()
    {
        mListener.startToolbarTimer();
    }

    /***
     * This function updates the toolbar.
     */
    @Override
    public void updateToolbar()
    {
        if (mChapterPresenter != null)
        {
            mChapterPresenter.updateReaderToolbar();
        }
    }

    /***
     * This function updates the header toolbar.
     *
     * @param aMangaTitle
     * @param aChapterTitle
     * @param aSize
     * @param aPage
     */
    @Override
    public void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage, int aChapterPosition)
    {
        mListener.updateToolbar(aMangaTitle, aChapterTitle, aSize, aPage, aChapterPosition);
    }

    /***
     * This function updates the current page.
     *
     * @param aPosition
     */
    @Override
    public void updateCurrentPage(int aPosition)
    {
        mListener.updateCurrentPage(aPosition);
    }

    /***
     * This function handles the viewpager refresh.
     */
    @Override
    public void onRefresh()
    {
        mChapterPresenter.onRefresh(0);
    }

    @Override
    public void setContentText(String aText)
    {
        mTextView.setText(aText);
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aContext
     */
    @Override
    public void onAttach(Context aContext)
    {
        super.onAttach(aContext);
        if (aContext instanceof Listeners.ReaderListener) mListener = (Listeners.ReaderListener) aContext;
        else throw new ClassCastException(aContext.toString() + " must implement Listeners.ReaderListener");
    }

    /***
     * This function creates the fragment.
     *
     * @param aSavedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        mChapterPresenter = new ChapterNovelPresenter(this, getArguments());
    }

    /***
     * This function initializes the view of the fragment.
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.reader_fragment_novel_item, aContainer, false);
        ButterKnife.bind(this, lView);
        mTextView.setUserGesureListener(this);

        mTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSingleTap();
            }
        });

        return lView;
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aSavedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle aSavedInstanceState)
    {
        super.onActivityCreated(aSavedInstanceState);
        if (aSavedInstanceState != null)
        {
            mChapterPresenter.onRestoreState(aSavedInstanceState);
        }
    }

    /***
     * This function is called in the fragment lifecycle.
     */
    @Override
    public void onStart()
    {
        mChapterPresenter.init(getArguments());
        super.onStart();
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mChapterPresenter.onResume();
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    public void onSaveInstanceState(Bundle aSave)
    {
        super.onSaveInstanceState(aSave);
        if (mChapterPresenter != null)
        {
            mChapterPresenter.onSaveState(aSave);
        }
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mChapterPresenter.onPause();
    }

    /***
     * This function trims Glide cache when low memory.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mChapterPresenter.onDestroy();
    }
}
