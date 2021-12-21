package com.example.serviceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

public class BootUpReceiver extends BroadcastReceiver {

    final long intervalMs = 600; // Интервал в миллисекундах

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, ServiceApp.class));
        context.startService(new Intent(context, ServiceRestartApp.class));
        setupAlarm(context);


//        Intent alarm = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        System.out.println("[*******-----------> Autostart  <-----------*******]");
//
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 15000, pendingIntent);


//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, ServiceApp.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 6000, 6000, pi);

//        Intent in = new Intent(context, MainActivity.class) ;
//        context.startActivity(in);

//        Intent i = new Intent(this, MyMainActivity.class);
//        i.setAction(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//        startActivity(i);
//        Intent tsd = new Intent("com.treedo.taburetka.tsd") ;
//
//        PackageManager pac = context.getPackageManager() ;
//        Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//
//        if (launchIntent != null) {
//            context.startActivity(launchIntent);
//        }

    }

    private void setupAlarm(Context context) {

        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, BootUpReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMs, pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMs, pi);
                }
            }
        } catch (Exception e) {

        }
    }
}