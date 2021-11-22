package com.example.serviceapp;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.TaskInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Trace;
import android.provider.Settings;
import android.telephony.DataFailCause;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

    Intent intent = new Intent() ;

    Thread workThread = null;

//    @Override
//    public void onCreate() {
//
////      Random random = new Random() ;
//        Date date = Calendar.getInstance().getTime();
//        DateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss") ;
//
//        String reportDate = formatDate.format(date);
//        reportDate = reportDate + ".txt" ;
//
//        writeFile(reportDate, new String("on")) ;
//
//
//        DownloadFile(reportDate, reportDate);
//    }

    public boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pm.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void checkPowerOn1c() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss") ;

                    Date d = new Date();

                    String dayOfTheWeek = sdf.format(d) + ".txt";
                    String timeOfTheDay = formatDate.format(d) + "\n" ;

                    downloadFiles(dayOfTheWeek, timeOfTheDay) ;

                    checkPowerOn1c() ;
                    Log.i("TIME_LOG", timeOfTheDay);
                    Thread.sleep(20000);

                }

            }catch (InterruptedException iex) {
            }

            workThread = null;
        }
    };

public void downloadFiles(String name_file, String data) {

    File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)));
    Writer wr = null;
    FileOutputStream fos = null;
    boolean isHaveInstanceProccesTSD = false ;

    try {

        final String tsdTaburetka = "com.treedo.taburetka.tsd" ;

        path.mkdirs();
        wr = new OutputStreamWriter(new FileOutputStream(new File(path, name_file),true));
        wr.write(data);

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

        HashMap pMap = new HashMap<String, Integer>();
        String[] processLinesAr = sb.toString().split("\n");
        for(String line : processLinesAr) {

            String[] comps = line.split("[\\s]+");

            if (comps.length != 9) {
                String packageName = comps[5] ;
                Log.i("tututut", packageName) ;
            }else {

                int pid = Integer.parseInt(comps[1]);
                String packageName = comps[8] ;
                if (packageName.equals(tsdTaburetka)){
                   isHaveInstanceProccesTSD =true ;
                }
                pMap.put(packageName, pid);
                Log.i("taburetka_tututu", packageName + " " + pid) ;
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
    public void writeFile(String reporteDate, String fileName){

        FileOutputStream fos = null;

        try {

            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(reporteDate.getBytes());

        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void DownloadFile(String fileURL, String fileName) {

        try {

            File root = Environment.getExternalStorageDirectory();

            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            FileOutputStream f = new FileOutputStream(new File(root, fileName));

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }

            f.close();

        } catch (Exception e) {
            Log.d("Downloader", e.getMessage());
        }
    }

    public static void writetoFile(String file, String text) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(String.valueOf(text));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
