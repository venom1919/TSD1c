package com.example.serviceapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static android.app.PendingIntent.getActivity;

public class ServiceApp extends Service {

    private final static String FILE_NAME = "content.txt";

    public static long firstCall1c ;

    boolean startedCheck1c = false ;
    Thread workThread = null;
    double latitude;
    double longitude;
    boolean locationisOn = true;

    @Override
    public void onCreate() {

        super.onCreate();
    }

    private void getLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isPassiveProvider = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            isNetworkEnabled = true;
        } else {
            isNetworkEnabled = false;
        }

        Log.i("isGPSEnabled ", String.valueOf(isGPSEnabled) + " isNetwork" + String.valueOf(isNetworkEnabled) + " isPass" + String.valueOf(isPassiveProvider));

        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();

                latitude = location.getLatitude();
                longitude = location.getLongitude();

//                JSONObject json = new JSONObject() ;
//                try {
//                    json.put(String.valueOf(new Date()), "Dolg." + latitude + " "  + "Shir." + longitude) ;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                System.out.println("position" + String.valueOf(latitude + " " + longitude));
                location.reset();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("onStusChade", provider);
            }

            public void onProviderEnabled(String provider) {
                Log.i("onStusChade", provider);
            }

            public void onProviderDisabled(String provider) {
                Log.i("provider","false");
            }
        };

        try {

            if (isNetworkEnabled) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());

            }else{

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 10, locationListener , Looper.getMainLooper());
            }

        } catch (NullPointerException ex) {

            ex.printStackTrace();
        }
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
        }
        catch(PackageManager.NameNotFoundException e) {
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

        Bundle extras = intent.getExtras();

        firstCall1c = extras.getLong("Time_TSD") ;
        System.out.println(firstCall1c);

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
    }

    ///////Write files date now
    public void downloadFiles(String name_file, String data, String dateForLocation)  {

        locationisOn = locationIsActive() ;

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(today);

        File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS))) ;
        String name_pathToFile = path + "/" + name_file ;

        System.out.println(name_pathToFile);

        getLocation() ;
        File f = new File(name_pathToFile);

        if(f.exists() && !f.isDirectory()) {

            List<LogsTerminal> detailsList = new ArrayList<>();
            detailsList.add(new LogsTerminal(formattedDate, String.valueOf(locationisOn), String.valueOf(latitude + " " + longitude)));
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
//                jsonObject1.addProperty("longitude" ,String.valueOf(longitude));

                jsonArray.add(jsonObject1);
                jsonObject.add("logs" ,jsonArray);
                fr.write(jsonObject.toString());
                fr.flush();
//              fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

     boolean isHaveInstanceProccesTSD = false ;

        try{

            String pathTo1c = "/data/app/com.treedo.taburetka.tsd" ;
            final String tsdTaburetka = "com.treedo.taburetka.tsd" ;

            String [] arr = new String[]{
                "/data",
                    "-c",
                    "ls /etc | grep com.treedo.taburetka.tsd"
            } ;

            String myVersion = Build.VERSION.RELEASE ;
            double x = (double)Double.valueOf(myVersion) ;

            System.out.println("calc_time "+ ServiceApp.firstCall1c);
            if (x>=7 & firstCall1c != 0){
                System.out.println("time is long  " + System.currentTimeMillis());
                checkPositiveStatus1c(System.currentTimeMillis()) ;
                return;
            }

            String[] command = {"/system/bin/app_process32"};

            Process p = Runtime.getRuntime().exec("ps aux");
            p.waitFor();
            StringBuffer sb = new StringBuffer();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            int ch;
            char[] buf = new char[1024];
            while ((ch = isr.read(buf)) != -1) {
                sb.append(buf, 0, ch);
        }

        String[] processLinesAr = sb.toString().split("\n");

        for(String line : processLinesAr) {

           // System.out.println("ps -returned " + " " + line + " ");
            String[] comps = line.split("[\\s]+");

            if (comps.length != 9) {
                String packageName = comps[0] ;
                System.out.println("pkg_1" + " " + packageName) ;
            }else {

                //int pid = Integer.parseInt(comps[1]);
                String packageName = comps[8] ;
                if (packageName.equals(tsdTaburetka)){
//                    System.out.println("pkg_2" + "" + packageName) ;
                    isHaveInstanceProccesTSD = true ;
                }
//                pMap.put(packageName, pid);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
//        if(!isHaveInstanceProccesTSD){
//            PackageManager pac = getPackageManager() ;
//            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
//            startActivity(launchIntent);
//        }
}

    public void checkPositiveStatus1c(long lastDate) {

//        Intent intent = new Intent();
//        intent.setAction("com.treedo.taburetka.tsd");
//        sendBroadcast(intent);

        if (!startedCheck1c){

//            firstCall1c = System.currentTimeMillis();
            System.out.println("firstCall1c " +  firstCall1c);
            startedCheck1c = true ;
        }

        System.out.println("firstCall1c " + firstCall1c);

        long lastChecked = lastDate - firstCall1c ;


//        System.out.println("lastChecked" + lastChecked);

        if(lastChecked > 180000){

            PackageManager pac = getPackageManager() ;
            Intent launchIntent = pac.getLaunchIntentForPackage("com.treedo.taburetka.tsd");
            startActivity(launchIntent);
            startedCheck1c = false ;
        }

    }


    public String getProcces(BufferedReader reader) {

    String output = "";
    String line = "";

    while (true) {

        try {
            if (!((line = reader.readLine()) != null)) break;
        } catch (IOException e) {
            e.printStackTrace();
        }
        output += line + "";

        System.out.println(output);
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
            byte[] jsonData = new byte[0];

            File f = new File(fileName);
            byte[] buffer = new byte[(int)f.length()];
            FileInputStream is = new FileInputStream(fileName);
            is.read(buffer);
            is.close();
            jsonData = buffer ;
            details = objectMapper.readValue(jsonData, Details.class);

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

//    @JsonProperty("longitude")
//    private String longitude;

    @JsonProperty("powerOn")
    private String powerOn;



    public LogsTerminal() {
    }

    public LogsTerminal(String date, String powerOn,  String coordinates) {
        this.date = date;
        this.powerOn = powerOn;
        this.coordinates = coordinates  ;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public String getLogitude() {
//        return longitude;
//    }
//
//    public void setLogitude(String logitude) {
//        this.longitude = logitude;
//    }

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
