package com.example.serviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Tsd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsd);

    }

    @Override
    protected void onStart() {

//        ServiceApp.firstCall1c = System.currentTimeMillis() ;
        Intent tsdTimeWork = new Intent(this, ServiceApp.class) ;
        tsdTimeWork.putExtra("Time_TSD", System.currentTimeMillis()) ;
        startService(tsdTimeWork);

        finish();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}