package com.teioh.m_feed;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class RecentUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateRecent();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateRecent() {
        while (isMyServiceRunning(RecentUpdateService.class)) {
            try {
                //TODO
                //do updates
                //UPDATE MangaJoy recent updates method and database to have a boolean recent column, reset the column
                //every update and re update with new recent manga so we do not need to pull fresh from the website every time we create the view
                //and make our service update the list periodically
                //Thread.sleep(1000 * 60 * 10);       //sleep for ~ 10 mins
                Thread.sleep(5000);
                this.stopSelf();
            } catch (InterruptedException e) {
                Log.e("RecentUpdateService", "Thread interrupted");
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
