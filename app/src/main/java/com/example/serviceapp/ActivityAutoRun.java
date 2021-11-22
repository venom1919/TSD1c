package com.example.serviceapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ActivityAutoRun extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        PackageManager pac = getPackageManager() ;
//        Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//        startActivity(launchIntent);

        PackageManager pac = getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pac.getApplicationInfo("com.treedo.taburetka.tsd", 0);
            String namea = ai.name;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        CharSequence name = pac.getApplicationLabel(ai);
    }
}