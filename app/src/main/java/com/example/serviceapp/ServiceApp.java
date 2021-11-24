package com.example.serviceapp;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.TaskInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.Trace;
import android.provider.Settings;
import android.telephony.DataFailCause;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.app.PendingIntent.getActivity;

public class ServiceApp extends Service {

    private final static String FILE_NAME = "content.txt";

    Thread workThread = null;
    boolean TsdTaburetkaUa = true ;
    double latitude ;
    double longitude ;

    private void getLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isPassiveProvider = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()){
            isNetworkEnabled  = true ;
        }else{
            isNetworkEnabled = false ;
        }

        Log.i("isGPSEnabled " ,String.valueOf(isGPSEnabled) + " isNetwork" + String.valueOf(isNetworkEnabled)+ " isPass" + String.valueOf(isPassiveProvider)) ;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                Log.i("ISKSK", "sss");
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();

                 latitude = location.getLatitude();
                 longitude = location.getLongitude();

                Log.i("scgfw" ,String.valueOf(latitude) + " " + String.valueOf(longitude)) ;

//                JSONObject json = new JSONObject() ;
//                try {
//                    json.put(String.valueOf(new Date()), "Dolg." + latitude + " "  + "Shir." + longitude) ;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                location.reset();

            }

//            public void onStatusChanged(String provider, int status,Bundle extras) {
//                Log.i("onStusChade" ,"asdas");
//            }
//
//            public void onProviderEnabled(String provider) {
//                Log.i("onStusChade" ,"asdas");
//            }
//
//            public void onProviderDisabled(String provider) {
//                Log.i("onStusChade_2" ,"asdas");
//            }
        };

        String theBestProvider = "";
        String provider_inet = "";
        boolean haveBestProvider = false;
        Criteria criteria = new Criteria();
        android.location.Location loc ;


        try {

            if (isNetworkEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
                Log.i("IsNewtw", "net") ;
//            }else if (isPassiveProvider){
//                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
//                Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) ;
//                Log.i("IsPassive_1", "net") ;
//                Log.i("IsPassive_loc" ,String.valueOf(loc.getLatitude()) + " " + loc.getLongitude()) ;
            }else{

                Log.i("IsGPS_2", "net") ;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 10, locationListener , Looper.getMainLooper());

            }

//
//            List<String> providers = locationManager.getAllProviders();
//            for (String str : providers) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                android.location.Location loc_3 = locationManager.getLastKnownLocation(str);
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                Log.i("prov_12,", String.valueOf(loc_3.getLatitude() + " " +  loc_3.getLongitude()) + " " + str) ;
//            }

            theBestProvider = locationManager.getBestProvider(criteria, false);

        } catch (NullPointerException ex) {

            Log.i("SSSSSSSSSSSSS", ex.getMessage());
            ex.printStackTrace();
        }


    }

    public boolean isAppInstalled(String packageName) {

        PackageManager pm = getPackageManager();

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pm.getApplicationInfo(packageName, 0).enabled;
        }
        catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        getLocation() ;

        if (workThread == null) {
            workThread = new Thread(run);
            workThread.start();
        }
        return Service.START_STICKY;
    }

    final Runnable run = new Runnable() {

        @Override
        public void run() {

            try {
                while(true){

//                    getLocation() ;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss") ;

                    Date d = new Date();

                    String dayOfTheWeek = sdf.format(d) + ".json";
                    String timeOfTheDay = formatDate.format(d) + "\n" ;
                    String dateForLocation = formatDate.format(d) ;
                    downloadFiles(dayOfTheWeek, timeOfTheDay ,dateForLocation) ;

                    Log.i("TIME_LOG", timeOfTheDay);
                    Thread.sleep(20000);
                }

            }catch (InterruptedException iex) {
                iex.printStackTrace();
            }

            workThread = null;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("On_Destroy_Tsd", String.valueOf(new Date())) ;
    }

    ///////Write files date now
    public void downloadFiles(String name_file, String data, String dateForLocation) {

    File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)));
    Writer wr = null;
    //FileOutputStream fos = null;
    boolean isHaveInstanceProccesTSD = false ;

    getLocation() ;

    String dta = "" ;
    JSONObject json = new JSONObject() ;

    try {
        Date nowDate = new Date() ;
        json.put(dateForLocation,  "latitude " + latitude + " "  + "longitude " + longitude) ;
        dta = json.toString() ;
    } catch (JSONException e) {
            e.printStackTrace();
        }

    try {
        path.mkdirs();
        wr = new OutputStreamWriter(new FileOutputStream(new File(path, name_file),true));
        wr.write(dta);
        wr.flush();
//        wr.close();

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    try{
        final String tsdTaburetka = "com.treedo.taburetka.tsd" ;

        /////TSD
        Process p = Runtime.getRuntime().exec("ps");
        p.waitFor();
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(p.getInputStream());
        int ch;
        char[] buf = new char[1024];
        while ((ch = isr.read(buf)) != -1) {
            sb.append(buf, 0, ch);
        }

//        HashMap pMap = new HashMap<String, Integer>();
        String[] processLinesAr = sb.toString().split("\n");

        for(String line : processLinesAr) {

            String[] comps = line.split("[\\s]+");

            if (comps.length != 9) {
                String packageName = comps[5] ;
            }else {

                int pid = Integer.parseInt(comps[1]);
                String packageName = comps[8] ;
                if (packageName.equals(tsdTaburetka)){
                   isHaveInstanceProccesTSD =true ;
                }
//                pMap.put(packageName, pid);
            }
        }

    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
        if(!isHaveInstanceProccesTSD){
            PackageManager pac = getPackageManager() ;
            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
            startActivity(launchIntent);
        }

}
//    public void writeFile(String reporteDate, String fileName){
//
//        FileOutputStream fos = null;
//
//        try {
//
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(reporteDate.getBytes());
//
//        } catch (IOException ex) {
//
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//
//        } finally {
//
//            try {
//                if (fos != null)
//                    fos.close();
//            } catch (IOException ex) {
//                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    public void DownloadFile(String fileURL, String fileName) {
//
//        try {
//
//            File root = Environment.getExternalStorageDirectory();
//
//            URL u = new URL(fileURL);
//            HttpURLConnection c = (HttpURLConnection) u.openConnection();
//            c.setRequestMethod("GET");
//            c.setDoOutput(true);
//            c.connect();
//
//            FileOutputStream f = new FileOutputStream(new File(root, fileName));
//
//            InputStream in = c.getInputStream();
//
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            while ((len1 = in.read(buffer)) > 0) {
//                f.write(buffer, 0, len1);
//            }
//
//            f.close();
//
//        } catch (Exception e) {
//            Log.d("Downloader", e.getMessage());
//        }
//    }

//    public static void writetoFile(String file, String text) {
//        try {
//            FileWriter fw = new FileWriter(file);
//            fw.write(String.valueOf(text));
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
