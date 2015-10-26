package com.teioh.m_feed.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.teioh.m_feed.Pojo.UpdateListEvent;

public class ParsePushReciever extends ParsePushBroadcastReceiver {

    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        BusProvider.getInstance().register(this);
        //set to load tab1? or object fragment ( not implemented )
        Log.e("PUSH", "recieved push");
        BusProvider.getInstance().post(new UpdateListEvent());
        BusProvider.getInstance().unregister(this);
    }
}
