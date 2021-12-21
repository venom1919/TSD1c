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
    protected void onStart() {
        System.out.println("parasha");
        finish();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void closeContextMenu() {
        super.closeContextMenu();
    }

    @Override
    public void closeOptionsMenu() {
        super.closeOptionsMenu();
    }





}