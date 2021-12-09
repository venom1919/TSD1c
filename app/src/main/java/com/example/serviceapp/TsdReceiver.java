package com.example.serviceapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class TsdReceiver extends BroadcastReceiver {

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        System.out.println("baddd");
        return super.peekService(myContext, service);
    }

    public TsdReceiver() {
        super();
        System.out.println("SSSSSSS");
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(":start receiver");
        Bundle bundle = intent.getExtras();

        if(null == bundle)
            return;

        boolean isPresent = intent.getBooleanExtra("present", false);
        String technology = intent.getStringExtra("technology");
        int plugged = intent.getIntExtra("plugged", -1);
        int scale = intent.getIntExtra("scale", -1);
        int health = intent.getIntExtra("health", 0);
        int status = intent.getIntExtra("status", 0);
        int rawlevel = intent.getIntExtra("level", -1);
        int level = 0;

        Log.d("Debug","Battery Receiver OnReceive");

        if(isPresent) {
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;

                Log.d("Debug","BatterReceiver: " + level);

                Toast.makeText(context,"Battery Receiver: " + level + "\t" + status + "Raw: " + rawlevel,Toast.LENGTH_LONG).show();

                if(level <60) {
                    /*
                     * Only invoke the service when level below threshold
                     */
//                    Intent i = new Intent(context, BatteryService.class);
//                    i.putExtra("level", level);
//                    context.startService(i);
                }
            }
        }
    }
}