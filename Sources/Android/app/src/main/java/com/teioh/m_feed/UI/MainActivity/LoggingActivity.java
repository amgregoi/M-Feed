package com.teioh.m_feed.UI.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.MangaLogger;

/***
 * This class is to give me access to my personal logging to check its status while on my personal phone
 */
public class LoggingActivity extends AppCompatActivity
{
    public final static String TAG = LoggingActivity.class.getSimpleName();

    private ListView mLogs;
    private ArrayAdapter<String> mAdapter;
    private FloatingActionButton mRefresh;

    /***
     * This function creates and returns a new intent for this activity.
     * @param aContext
     * @return
     */
    public static Intent getNewInstance(Context aContext)
    {
        Intent lIntent = new Intent(aContext, LoggingActivity.class);
        return lIntent;
    }

    /***
     * This function initializes the view of the activity.
     * @param aSavedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.logging_fragment);
        mLogs = (ListView) findViewById(R.id.logs);
        mAdapter = new ArrayAdapter<String>(this, R.layout.logging_item, R.id.log_item, MangaLogger.getLogs());
        mLogs.setAdapter(mAdapter);

        mRefresh = (FloatingActionButton) findViewById(R.id.refresh_log);
        mRefresh.setOnClickListener(v -> {
            mAdapter = new ArrayAdapter<String>(this, R.layout.logging_item, R.id.log_item, MangaLogger.getLogs());
            mLogs.setAdapter(mAdapter);
        });
    }


}
