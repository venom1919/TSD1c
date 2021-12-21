package com.example.serviceapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class TsdReceiver extends BroadcastReceiver {

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public TsdReceiver() {
        super();
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle build = intent.getExtras();

        try {

            boolean getInfoByTSD = build.getBoolean("TSD");

            if (getInfoByTSD){

                Intent tsdTimeWork = new Intent(context, ServiceApp.class) ;
                tsdTimeWork.putExtra("Time_TSD", System.currentTimeMillis()) ;
                context.startService(tsdTimeWork);

            }

        }catch (Exception ex){

        }
}

}
