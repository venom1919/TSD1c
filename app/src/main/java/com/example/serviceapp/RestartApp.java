package com.example.serviceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class RestartApp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      Log.i("sda12321", "sdasdas");
//      if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

          Intent intent1 = new Intent(context, ServiceRestartApp.class) ;
          intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
          context.startActivity(intent1);

      //  }

            ActivityManager as = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            Intent i = new Intent(context, ServiceRestartApp.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

//    }

    }

}