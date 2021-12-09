package com.example.serviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        PackageManager pm = getPackageManager();
//        ComponentName pluggedReceiver =
//                new ComponentName(getApplicationContext(), MainActivity.class);
//        ComponentName unpluggedReceiver = new ComponentName(getApplication(), MainActivity.class);
//
//        pm.setComponentEnabledSetting(
//        pluggedReceiver,
//        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//        PackageManager.DONT_KILL_APP);
//
//        pm.setComponentEnabledSetting(
//        unpluggedReceiver,
//        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//        PackageManager.DONT_KILL_APP);

//        PackageManager pm = getPackageManager();
//        pm.setComponentEnabledSetting(null
//        ,  PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//        PackageManager.DONT_KILL_APP);

//        Intent intent = new Intent("com.treedo.taburetka.tsd") ;
//        startActivity(intent) ;

        ////POWER_On
        Intent intentPowerOn = new Intent(this, ServiceApp.class) ;
        startService(intentPowerOn);

//        Intent in = new Intent("com.treedo.taburetka.tsd");

//        sendBroadcast(new Intent(this, TsdReceiver.class).setAction("com.treedo.taburetka.tsd"));

        //        TsdReceiver tsdReceiver  = new TsdReceiver() ;
//
//        registerReceiver(tsdReceiver, new IntentFilter("com.treedo.taburetka.tsd"));
//        tsdReceiver.onReceive(this ,new Intent());
//        Intent intentTsd = new Intent(this, TsdService.class) ;
//        startService(intentTsd);




//        ////POWER_On
//        Intent intentPowerOn1 = new Intent(this, ServiceRestartApp.class) ;
//        startService(intentPowerOn1);

//        ////POWER_On
//        Intent intentRestartApp = new Intent(this, ServiceRestartApp.class) ;
//        startService(intentRestartApp);


//        Intent intent = new Intent(this, ActivityAutoRun.class) ;
//        startActivity(intent);

           //////1c
           PackageManager pac = getPackageManager() ;
           Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
           startActivity(launchIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}