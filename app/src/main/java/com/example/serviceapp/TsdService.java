package com.example.serviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TsdService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("ssss");
        Intent intent1 = new Intent("com.treedo.taburetka.tsd") ;
        String name = intent1.getAction() ;

        System.out.println(name);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {

        System.out.println("destroy tsd");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
