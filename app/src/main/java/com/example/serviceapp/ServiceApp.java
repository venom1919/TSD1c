package com.example.serviceapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GnssAntennaInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
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
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.location.LocationManager;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static java.security.AccessController.getContext;

public class ServiceApp extends Service {

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
    LocationManager locationManager;
    boolean simCardActive ;  ;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void getLocation1() {

//        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        List<String> lProviders = locationManager.getProviders(false);
//        for (int i = 0; i < lProviders.size(); i++) {
//            Log.d("LocationActivity", lProviders.get(i));
//        }
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        LocationListener locationListenerGPS = location -> {
//            System.out.println("msg123");
//
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
//            System.out.println(msg);
//
//            location.reset();
//        };

//        System.out.println("get best" + provider);
//        long minTime = 60000;
//        float minDistance = 5;
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        System.out.println("324234");
//        locationManager.requestLocationUpdates(provider, Long.MIN_VALUE, Float.MAX_VALUE, locationListenerGPS, Looper.getMainLooper());

    }

    public void getAPNType() {

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        getStrengthCellSignal(mTelephony, networkInfo != null);

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

//        getStrengthCellSignal(mTelephony, I);

    }

    public void getStrengthCellSignal(TelephonyManager mTelephony, boolean internetActive) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String strength ;
        List<CellInfo> cellInfos = mTelephony.getAllCellInfo();

        if (cellInfos != null) {
            for (int i = 0; i < cellInfos.size(); i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        simCardActive = true;
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        int levelSignal = cellSignalStrengthWcdma.getAsuLevel() - 115;

                        if (levelSignal < 0 && levelSignal >= -70) {
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Excellent, " + "Internet " + internetActive);
                            System.out.println(levelSignal + " " + "Excellent");
                        } else if (levelSignal <= -71 && levelSignal >= -85) {
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Good," + "Internet " + internetActive);
                            System.out.println(levelSignal + " " + "Good");
                        } else if (levelSignal <= -86 && levelSignal >= -100) {
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Fair, "+ "Internet " + internetActive);
                            System.out.println(levelSignal + " " + "Fair");
                        } else if (levelSignal <= -101 && levelSignal >= -109) {
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "bad, " + "Internet " +internetActive );
                        } else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "3G DBM: " + levelSignal + " " + "Discconected, " + "Internet " + internetActive);
                        }

                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        simCardActive = true;
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();

                        int levelSignal = cellSignalStrengthGsm.getAsuLevel() - 113;

                        if (levelSignal < 0 && levelSignal >= -70) {
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Excellent, " + "Internet " +internetActive);
                            System.out.println(levelSignal + " " + "Excellent");
                        } else if (levelSignal <= -71 && levelSignal >= -85) {
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Good, " + "Internet " +internetActive);
                            System.out.println(levelSignal + " " + "Good");
                        } else if (levelSignal <= -86 && levelSignal >= -100) {
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Fair, " + "Internet " +internetActive);
                            System.out.println(levelSignal + " " + "Fair");
                        } else if (levelSignal <= -101 && levelSignal >= -109) {
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "bad, "+ "Internet " +internetActive);
                        } else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "EDGE DBM: " + levelSignal + " " + "Discconected, "+ "Internet " +internetActive);
                        }

                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        simCardActive = true;
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);

                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        String[] mass = cellInfoLte.toString().split(" ");

                        char[] arr = mass[10].toCharArray();
                        ArrayList<Integer> n = new ArrayList();
//                        int size = 0;
                        for (Character c : arr) {
                            if (Character.isDigit(c)) {
                                n.add(Character.getNumericValue(c));
//                                size++;
                            }
                        }

                        StringBuilder strBigNum = new StringBuilder();

                        for (int str : n) {
                            strBigNum.append(str);
                        }

                        int bigNum = 0;
                        int factor = 1;
                        for (int ss = strBigNum.length() - 1; ss >= 0; ss--) {
                            bigNum += Character.digit(strBigNum.charAt(ss), 10) * factor;
                            factor *= 10;
                        }

//                        int x = 0;
//                        for (int s = 0; s < n.size(); s++) {
//                            x += Integer.bitCount(n.get(s));
//                        }
                        int levelSignal = bigNum - 141;

                        if (levelSignal < 0 && levelSignal >= -70) {
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Excellent, " + "Internet " +internetActive);
                            System.out.println(levelSignal + " " + "Excellent");
                        } else if (levelSignal <= -71 && levelSignal >= -85) {
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Good, "  + "Internet " +internetActive );
                            System.out.println(levelSignal + " " + "Good");
                        } else if (levelSignal <= -86 && levelSignal >= -100) {
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Fair, " + "Internet " +internetActive);
                            System.out.println(levelSignal + " " + "Fair");
                        } else if (levelSignal <= -101 && levelSignal >= -109) {
                            System.out.println(levelSignal + " " + "bad");
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "bad, " + "Internet " +internetActive);
                        } else {
                            System.out.println(levelSignal + " " + "Discconected");
                            typeNetworkIsActive.put("InformationSignal", "LTE DBM: " + levelSignal + " " + "Discconected, " + "Internet " +internetActive);
                        }

                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthCdma.getDbm());

                        System.out.println(cellSignalStrengthCdma.toString());
                        simCardActive = true ;
                    }
                }
            }
        } else {

            typeNetworkIsActive.put("InformationSignal", " Internet not work");
            simCardActive = false ;
        }

        mTelephony = null;
    }

    public void getStatusNetwork() {

        NetworkRequest networkRequest = null;
        ConnectivityManager.NetworkCallback networkCallback = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
                    boolean hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        }

    }

    public double getSpeedNetwork() {
        return Double.parseDouble(null);
    }

    private void getLocation() {

        //ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

         //     NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//      System.out.println(networkInfo.getState() + " " + networkInfo.getTypeName());

        LocationListener locationListenerTSD = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                location.getAccuracy() ;
                latitude = location.getLatitude();
                longitude = location.getLongitude() ;
                location.reset();
            }
        };

        LocationManager locationManagerT = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManagerT.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

//        List<GnssAntennaInfo> lo = locationManagerT.getGnssAntennaInfos() ;
//        for (GnssAntennaInfo s : lo){
//            System.out.println(s);
//        }

        locationManagerT.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.MIN_VALUE, 0, locationListenerTSD, Looper.getMainLooper());

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManagerT.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,0, locationListenerTSD, Looper.getMainLooper());

//        Location imHere = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

//        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean isPassiveProvider = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        List<String> lProviders = locationManager.getProviders(false);
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Location loc = locationManager.getLastKnownLocation(provider);
//
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
//            @Override
//            public void onFlushComplete(int requestCode) {
//                System.out.println("asdasdf");
//            }
//
//            public void onLocationChanged(Location location) {
//
//
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//
//                Log.d("sss_121",  String.valueOf(longitude) + " " + String.valueOf(latitude));
////                coord.put(latitude,longitude);
//
////                JSONObject json = new JSONObject() ;
////                try {
////                    json.put(String.valueOf(new Date()), "Dolg." + latitude + " "  + "Shir." + longitude) ;
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//            }
//
//                location.reset();
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.i("onStusChade", provider);
//            }
//
//            public void onProviderEnabled(String provider) {
//                Log.i("onStusChade", provider);
//            }
//            public void onProviderDisabled(String provider) {
//                Log.i("provider", "false");
//            }
//
//        };
//
//        try {
//
////            if (isNetworkEnabled) {
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                System.out.println(provider);
//                Location location = locationManager.getLastKnownLocation(provider);
//                System.out.println(location.getProvider() + location.getLatitude() + " " + location.getLongitude());
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, Looper.getMainLooper());
//                //Location loc = locationManager.getLastKnownLocation(provider) ;
//
////            }else{
//
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
////                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ;
////                latitude = loc.getLatitude() ;
////                longitude = loc.getLongitude() ;
////            }
//
//        } catch (NullPointerException ex) {
//
//            ex.printStackTrace();
//        }

    }

    public void writesTimeWorkTSD() {

        try {

            count = count +1 ;
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
//                    writesTimeWorkTSD() ;
                    getAPNType() ;
//                    getStatusNetwork() ;
                    getLocation();
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

//        getLocation() ;
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



