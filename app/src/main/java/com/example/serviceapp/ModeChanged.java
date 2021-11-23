package com.example.serviceapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

public class ModeChanged extends BroadcastReceiver {

    private final static String TAG = "LocationProviderChanged";
    boolean isGPSEnabled ;
    boolean isNetworkEnabled ;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

//            Intent intent1 = new Intent(context, ServiceApp.class) ;
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
//            context.startActivity(intent1);

            Log.i("TAG_modeChanged", "Location Providers Changed");

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Toast.makeText(context, "GPS Enabled: " + isGPSEnabled + " Network Location Enabled: " + isNetworkEnabled, Toast.LENGTH_LONG).show();

            if (!isGPSEnabled) {

            }
        }
    }


}