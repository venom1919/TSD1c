package com.example.serviceapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ServiceRestartApp extends Service {

    @Override
    public void onCreate() {

        Intent tsd = new Intent("com.treedo.taburetka.tsd") ;

        PackageManager pac = getPackageManager() ;
        Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");

        if (launchIntent != null) {
            startActivity(launchIntent);
        }

        Intent intentPowerOn = new Intent(this, ServiceApp.class) ;
        startService(intentPowerOn);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ServiceRestartApp() {
        super();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

