package com.example.serviceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Tsd extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_tsd);
//    }

//    @Override
//    protected void onStart() {
//
////        startService(new Intent( Tsd.this, TsdService.class));
//
////        Intent tsdTimeWork = new Intent(this, ServiceApp.class) ;
////        tsdTimeWork.putExtra("Time_TSD", System.currentTimeMillis()) ;
////        System.out.println("titututi");
////        startService(tsdTimeWork);
//
//        finish();
//        super.onStart();
//    }


    @Override
    protected void onStart() {
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