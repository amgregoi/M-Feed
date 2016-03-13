package com.teioh.m_feed.Utils;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

public class ParsePushReciever extends ParsePushBroadcastReceiver {

    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
//        BusProvider.getInstance().register(this);
//        BusProvider.getInstance().post(new UpdateListEvent());
//        BusProvider.getInstance().unregister(this);
    }
}
