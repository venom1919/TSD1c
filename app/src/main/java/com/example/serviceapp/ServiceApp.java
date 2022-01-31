package com.example.serviceapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class ServiceApp extends Service {


//  private final static String FILE_NAME = "content.txt";

    public static long firstCall1c = 0;
    int count = 0;
    Thread workThread = null;
    double latitude;
    double longitude;
    boolean locationisOn = true;
    long milliseconds;
    boolean networkIsOn;
    private LocationCallback locationCallback;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    Location mCurrentLocation;
    double mLatitude = 0, mLongitude = 0;
    private FusedLocationProviderClient mFusedLocationClient1;
    private FusedLocationProviderClient mFusedLocationProviderClient = null;
    private LocationRequest locationRequest = null;
    private LocationCallback mLocationCallback = null;
    private final long LOCATION_REQUEST_INTERVAL = 5000;
    public static int netType = 0;
    public Map<String, String> typeNetworkIsActive = new HashMap<>();
    String network;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void getLocation() {

        LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);

            System.out.println(l.getLatitude() + l.getLongitude());
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
        }

        mFusedLocationClient1 = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {


            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double wayLatitude = location.getLatitude();
                        double wayLongitude = location.getLongitude();
                    }
                }
            }
        };
    }

    public void getAPNType() {

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

//
//        if (networkInfo == null) {
//            typeNetworkIsActive.put("Internet", "NOT_WORK");
//        }
//
//        int nType = networkInfo.getType();
//        if (nType == ConnectivityManager.TYPE_MOBILE) {
//
//            int nSubType = networkInfo.getSubtype();
//
//            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
//                typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("UMTS"));
//            }
//
//            if (nSubType == TelephonyManager.NETWORK_TYPE_EDGE) {
//                typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("EDGE: 50-100 kbps"));
//            }
//
//            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE) {
//                typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("LTE: 10+ Mbps"));
//            }
//
//            if (nSubType == TelephonyManager.NETWORK_TYPE_HSPA) {
//                typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("3g-HSPA: 700-1700 kbps"));
//            }
//
//            if (nSubType == TelephonyManager.NETWORK_TYPE_HSPAP) {
//                typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("3g-HSPAP: 10-20 Mbp"));
//            }
//        }
//
//        if (nType == ConnectivityManager.TYPE_WIFI) {
//            typeNetworkIsActive.put(String.valueOf("Internet"), String.valueOf("WIFi: 0-∞"));
//        }

        getStrengthCellSignal(mTelephony);

    }

    public void getStrengthCellSignal(TelephonyManager mTelephony) {


        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

//        System.out.println(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
//        System.out.println(Build.VERSION.SDK_INT + " " + Build.VERSION_CODES.P);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//           SignalStrength signalStrength = telephonyManager.getSignalStrength();
//           System.out.println("signal cccl " + signalStrength.getLevel());
//        }

        String strength = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos = mTelephony.getAllCellInfo();

        if(cellInfos != null) {
            for (int i = 0 ; i < cellInfos.size() ; i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        int levelSignal = cellSignalStrengthWcdma.getAsuLevel() - 115 ;

                        if (levelSignal <0 && levelSignal >= -70){
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Excellent");
                            System.out.println(levelSignal + " " + "Excellent");
                        }else if(levelSignal<= -71 &&levelSignal>= -85){
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Good");
                            System.out.println(levelSignal + " " + "Good");
                        }else  if (levelSignal<= -86 && levelSignal >= -100){
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Fair");
                            System.out.println(levelSignal + " " + "Fair");
                        }else if(levelSignal <= -101 && levelSignal >= -109){
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "bad");
                        }
                        else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Discconected");
                        }

                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();

                        int levelSignal = cellSignalStrengthGsm.getAsuLevel()  - 113 ;

                        if (levelSignal <0 && levelSignal >= -70){
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Excellent");
                            System.out.println(levelSignal + " " + "Excellent");
                        }else if(levelSignal<= -71 &&levelSignal>= -85){
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Good");
                            System.out.println(levelSignal + " " + "Good");
                        }else  if (levelSignal<= -86 && levelSignal >= -100){
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Fair");
                            System.out.println(levelSignal + " " + "Fair");
                        }else if(levelSignal <= -101 && levelSignal >= -109){
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "bad");
                        }
                        else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Discconected");
                        }

                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthLte.getDbm());
                        String [] mass = cellInfoLte.toString().split(" ");

                        char [] arr = mass[10].toCharArray() ;
                        int[] n = new int[2];
                        int size = 0 ;
                        for (Character c : arr){
                            if(Character.isDigit(c)){
                                n[size] = Character.getNumericValue(c);
                                size ++ ;
                            }
                        }

                        StringBuilder strBigNum = new StringBuilder();

                        for (int str : n) {
                            strBigNum.append(str);
                        }

                        int bigNum = 0;
                        int factor = 1;
                        for (int ss = strBigNum.length()-1; ss >= 0; ss--) {
                            bigNum += Character.digit(strBigNum.charAt(ss), 10) * factor;
                            factor *= 10;
                        }

                        int x = 0 ;
                        for (int s = 0; s<n.length; s++) {
                            x += Integer.bitCount(n[s]);
                        }
                        int levelSignal = bigNum  - 141 ;



                        if (levelSignal <0 && levelSignal >= -70){
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Excellent");
                            System.out.println(levelSignal + " " + "Excellent");
                        }else if(levelSignal<= -71 &&levelSignal>= -85){
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Good");
                            System.out.println(levelSignal + " " + "Good");
                        }else  if (levelSignal<= -86 && levelSignal >= -100){
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Fair");
                            System.out.println(levelSignal + " " + "Fair");
                        }else if(levelSignal <= -101 && levelSignal >= -109){
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "bad");
                        }
                        else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Discconected");
                        }
                        n = null;

                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthCdma.getDbm());

                        System.out.println(cellSignalStrengthCdma.toString());
                        System.out.println("strength4 " + strength);
                        System.out.println("strength4_1 " + cellSignalStrengthCdma.getLevel());
                        System.out.println("strength4_2 " + cellSignalStrengthCdma.getAsuLevel());

                    }
                }
            }
        }else {



        }

        mTelephony = null ;
    }

    public void getStatusNetwork(){

        NetworkRequest networkRequest = null ;
        ConnectivityManager.NetworkCallback networkCallback = null ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                networkCallback = new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    boolean hasCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    System.out.println(hasCellular);
                    boolean hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    System.out.println(hasWifi);
                }
            };
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        }


    }

    public double getSpeedNetwork(){


        return Double.parseDouble(null);
    }

//      private void getLocation() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean isPassiveProvider = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//
////      String bestProvider = locationManager.getBestProvider(new Criteria(), true);
//
//        if (netInfo != null && netInfo.isConnected()) {
//            isNetworkEnabled = true;
//        } else {
//            isNetworkEnabled = false;
//        }
//
//        Log.i("isGPSEnabled ", String.valueOf(isGPSEnabled) + " isNetwork" + String.valueOf(isNetworkEnabled) + " isPass" + String.valueOf(isPassiveProvider));
//        LocationListener locationListener = new LocationListener() {
//
//            public void onLocationChanged(Location location) {
//
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//
//                coord.put(latitude,longitude);
//
////                JSONObject json = new JSONObject() ;
////                try {
////                    json.put(String.valueOf(new Date()), "Dolg." + latitude + " "  + "Shir." + longitude) ;
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//                location.reset();
//
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.i("onStusChade", provider);
//            }
//
//            public void onProviderEnabled(String provider) {
//                Log.i("onStusChade", provider);
//            }
//
//            public void onProviderDisabled(String provider) {
//                Log.i("provider", "false");
//            }
//        };
//
//        try {
//
//            if (isNetworkEnabled) {
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
//                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ;
//                latitude = loc.getLatitude() ;
//                longitude = loc.getLongitude() ;
//
//            }else{
//
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
//                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ;
//                latitude = loc.getLatitude() ;
//                longitude = loc.getLongitude() ;
//            }
//
//        } catch (NullPointerException ex) {
//
//            ex.printStackTrace();
//        }

//    }

    public void writesTimeWorkTSD() {

        try {

            count =count +1 ;

            if(count <4){
                return;
            }

            String timeStamp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) + "/" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            FileReader fr = new FileReader(timeStamp);
            BufferedReader reader = new BufferedReader(fr);
            String lines = reader.readLine().substring(1);

            if (lines != null) {
                count = count + 1 ;
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = f.parse(lines);
                milliseconds = d.getTime();

                System.out.println(System.currentTimeMillis() - milliseconds);
                if (System.currentTimeMillis() - milliseconds > 180000 & count>4) {
                    PackageManager pac = getPackageManager();
                    Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
                    startActivity(launchIntent);
                    count = 0  ;
                }

//                line = reader.readLine();

//                if (line != null) {
//                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date d = f.parse(line);
//                milliseconds = d.getTime();
//                System.out.println("now _____" + milliseconds);
//                if (System.currentTimeMillis() - milliseconds > 180000) {
//                PackageManager pac = getPackageManager();
//                Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//                startActivity(launchIntent);
//               }
//            }

            }


//            if (!line.equals(null)) {
//
//                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date d = f.parse(line);
//                milliseconds = d.getTime();
//                System.out.println("now _____" + milliseconds);
//
//                if (System.currentTimeMillis() - milliseconds > 180000) {
//                    PackageManager pac = getPackageManager();
//                    Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//                    startActivity(launchIntent);
//                }
//            }

        }catch(Exception e){
            PackageManager pac = getPackageManager();
            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
            startActivity(launchIntent);
            e.printStackTrace() ;
        }


//
////      String[] arrayTimeTsd = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS))).list((dir, name) -> new Date(new SimpleDateFormat("yyyy-MM-dd")));
////      File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS))) + String.valueOf(new SimpleDateFormat("yyyy-MM-dd")) ;
//        Time time;
//        Date date ;
////        SimpleDateFormat fmt = new SimpleDateFormat("");
//
//        FileInputStream fileInputStream = null ;
//        try {
//
////            fileInputStream = new FileInputStream(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)) + String.valueOf(new SimpleDateFormat("yyyy-MM-dd"))) ;
////            byte[] buff = new byte[fileInputStream.available()] ;
//            String line = new String();
//            Date data = new Date() ;
//
//            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//            System.out.println(timeStamp);
//            Scanner scan = new Scanner(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)) + "/" +timeStamp);
//
////            FileInputStream fin=new FileInputStream(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)) + "/" +timeStamp) ;
//
//
//            File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)) + "/" +timeStamp);
//
//            FileReader fr = new FileReader(file);
//            //создаем BufferedReader с существующего FileReader для построчного считывания
//            BufferedReader reader = new BufferedReader(fr);
//            // считаем сначала первую строку
//            String line = reader.readLine();
//            while (line != null) {
//                System.out.println(line);
//                // считываем остальные строки в цикле
//                line = reader.readLine();
//
//
//
//
//
//            while(scan.hasNextLine()) {
//                line = scan.nextLine();
//                System.out.println("time is file " + line);
//
//            }
//
//            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                Date d = f.parse(line);
//                 milliseconds = d.getTime();
//
//                 if (System.currentTimeMillis() - milliseconds > 180000){
//                     PackageManager pac = getPackageManager() ;
//                     Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//                     startActivity(launchIntent);
//                 }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
////            Date date1 = fmt.parse(line) ;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////      Date date = fmt.parse(dateString);
////        for (String str : arrayTimeTsd) {
////
//////            try {
//////                date = fmt.parse(str);
//////                System.out.println(date);
//////            } catch (ParseException e) {
//////                e.printStackTrace();
//////            }
////
////            System.out.println(str);
////        }

    }

    public boolean locationIsActive(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isAppInstalled(String packageName) {

        PackageManager pm = getPackageManager();

        try {

            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pm.getApplicationInfo(packageName, 0).enabled;

        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Bundle extras = intent.getExtras();
//
//        try {
//
//            firstCall1c = extras.getLong("Time_TSD") ;
//
//        }catch (NullPointerException ex){
//
//            firstCall1c = 0;
//        }

        if (workThread == null) {
            workThread = new Thread(run);
            workThread.start();
        }

        return Service.START_STICKY;
    }


    final Runnable run = new Runnable() {

        @Override
        public void run() {

            try{

                while(true){

//                    finalMTelephony.listen(new PhoneStateListener() {
//                        @Override
//                        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                            super.onSignalStrengthsChanged(signalStrength);
//                            System.out.println("Pess");
//                        }
//                    }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss") ;

                    Date d = new Date();

                    String dayOfTheWeek = sdf.format(d) + ".json";
                    String timeOfTheDay = formatDate.format(d) + "\n" ;
                    String dateForLocation = formatDate.format(d) ;
                    downloadFiles(dayOfTheWeek, timeOfTheDay, dateForLocation) ;
                    Log.i("TIME_LOG", timeOfTheDay);
//                    writesTimeWorkTSD() ;
                    getAPNType() ;
//                    getStatusNetwork() ;
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
    }

    ///////Write files date now
    public void downloadFiles(String name_file, String data, String dateForLocation)  {

        locationisOn = locationIsActive() ;

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(today);

        File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS))) ;
        String name_pathToFile = path + "/" + name_file ;

        Iterator it = typeNetworkIsActive.entrySet().iterator();

        for (Map.Entry<String, String> entry : typeNetworkIsActive.entrySet()) {
            network  = entry.getKey() + " " + entry.getValue();
        }


//        System.out.println(name_pathToFile);

        getLocation() ;
        File f = new File(name_pathToFile);

        if(f.exists() && !f.isDirectory()) {

            List<LogsTerminal> detailsList = new ArrayList<>();
            detailsList.add(new LogsTerminal(formattedDate, String.valueOf(locationisOn), String.valueOf(latitude) + " " + longitude, network));
            writeCourseList(detailsList, String.valueOf(path), name_pathToFile) ;

        }else {

            if(longitude == 0.0 || latitude == 0.0 ){
                return;
            }

            try (FileWriter fr = new FileWriter(name_pathToFile)){

                JsonObject jsonObject = new JsonObject();
                JsonArray jsonArray = new JsonArray() ;
                JsonObject jsonObject1 = new JsonObject() ;

                jsonObject1.addProperty("logs", formattedDate);
                jsonObject1.addProperty("powerOn", String.valueOf(locationisOn) );
                jsonObject1.addProperty("coordinates", String.valueOf(latitude + " " + longitude));

                jsonObject1.addProperty("network", network) ;

                jsonArray.add(jsonObject1);
                jsonObject.add("logs" ,jsonArray);
                fr.write(jsonObject.toString());
                fr.flush();
//              fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

     boolean itsNewTerminal = false ;
     boolean isHaveInstanceProccesTSD = false ;

        try{

            final String tsdTaburetka = "com.treedo.taburetka.tsd" ;

            String myVersion = Build.VERSION.RELEASE ;
            double x = (double)Double.valueOf(myVersion) ;
//            if (x>=6 & firstCall1c != 0){
//                itsNewTerminal = true;
//                System.out.println("time is long " + System.currentTimeMillis());
////                checkPositiveStatus1c() ;
//                writesTimeWorkTSD() ;
//            }
            if (x >=6.0){
                itsNewTerminal = true ;
                writesTimeWorkTSD() ;
            }else {
                Process p = Runtime.getRuntime().exec("ps");
                p.waitFor();
                StringBuffer sb = new StringBuffer();
                InputStreamReader isr = new InputStreamReader(p.getInputStream());
                int ch;
                char[] buf = new char[1024];
                while ((ch = isr.read(buf)) != -1) {
                    sb.append(buf, 0, ch);
                }

                String[] processLinesAr = sb.toString().split("\n");

                for (String line : processLinesAr) {

                    String[] comps = line.split("[\\s]+");
                    if (comps.length != 9) {
                        String packageName = comps[0];
                    } else {

                        String packageName = comps[8];
                        System.out.println(packageName);
                        if (packageName.equals(tsdTaburetka)) {
                            isHaveInstanceProccesTSD = true;
                        }
                    }
                }
            }
    }catch (Exception e) {
        e.printStackTrace();
    }
        if(!isHaveInstanceProccesTSD & !itsNewTerminal){
//            System.out.println("error my errpr  " + String.valueOf(itsNewTerminal));
            PackageManager pac = getPackageManager() ;
            System.out.println("isHaveInstanceProccesTSD");
            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
            startActivity(launchIntent);
        }
}

    public void checkPositiveStatus1c() {

        long lastChecked = System.currentTimeMillis() - firstCall1c ;

//        System.out.println("1cc_time" + lastChecked);
        if(lastChecked > 180000){
            PackageManager pac = getPackageManager() ;
            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
            startActivity(launchIntent);
            firstCall1c  = System.currentTimeMillis() ;
        }
    }

    public String getProcces(BufferedReader reader) {

    String output = "";
    String line = "";

    while (true) {

        try {
            if (!((line = reader.readLine()) != null))
                break;
        } catch (IOException e) {
            e.printStackTrace();
        }
        output += line + "";
    }

    return  output ;
}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void writeCourseList(List<LogsTerminal> detailsList, String path, String fileName) {

        Details details = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            byte[] jsonData ;

            File f = new File(fileName);

            byte[] buffer = new byte[(int) new File(fileName).length()] ;
//            byte[] buffer = new byte[(int)f.length()];
            FileInputStream is = new FileInputStream(fileName);
            is.read(buffer);
            is.close();
//            jsonData = buffer ;
            details = objectMapper.readValue(buffer, Details.class);

            List<LogsTerminal> existingCourseList = details.getDetailsList();
            if(null != existingCourseList && existingCourseList.size() > 0) {
                    for (Object l : detailsList){
                      existingCourseList.add((LogsTerminal) l) ;
                    }
                details.setDetailsList(existingCourseList);
            } else {
                details.setDetailsList(detailsList);
            }

            objectMapper.writeValue(new File(fileName), details);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Details {

    @JsonProperty("logs")
    private List<LogsTerminal> detailsList = new ArrayList<>();

    public List<LogsTerminal> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(List<LogsTerminal> detailsList) {
        this.detailsList = detailsList;
    }
}

class LogsTerminal{

    @JsonProperty("logs")
    private String date;

    @JsonProperty("coordinates")
    private String coordinates;

    @JsonProperty("powerOn")
    private String powerOn;

    @JsonProperty("network")
    private String network;

    public LogsTerminal() {

    }

    public LogsTerminal(String date, String powerOn, String coordinates, String network) {
        this.date = date;
        this.powerOn = powerOn;
        this.coordinates = coordinates  ;
        this.network = network ;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPowerOn() {
        return powerOn;
    }

    public void setPowerOn(String powerOn) {
        this.powerOn = powerOn;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setlatitude(String coordinates) {
        this.coordinates = coordinates;
    }
}
//public class MultiSimListener extends PhoneStateListener {
//
//    private Field subIdField;
//    private long subId = -1;
//
//    public MultiSimListener (long subId) {
//        super();
//        try {
//            // Get the protected field mSubId of PhoneStateListener and set it
//            subIdField = this.getClass().getSuperclass().getDeclaredField("mSubId");
//            subscriptionField.setAccessible(true);
//            subscriptionField.set(this, subId);
//            this.subId = subId;
//        } catch (NoSuchFieldException e) {
//
//        } catch (IllegalAccessException e) {
//
//        } catch (IllegalArgumentException e) {
//
//        }
//    }
//
//    @Override
//    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//        // Handle the event here, subId indicates the subscription id if > 0
//    }
//
//}



