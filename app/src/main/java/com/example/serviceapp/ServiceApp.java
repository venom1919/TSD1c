package com.example.serviceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.JsonWriter;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import static android.app.PendingIntent.getActivity;

public class ServiceApp extends Service {

    private final static String FILE_NAME = "content.txt";

    Thread workThread = null;
    boolean tsdTaburetkaUa = true;
    double latitude;
    double longitude;
    boolean locationisOn = true;


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

                Log.i("sssss232", String.valueOf(longitude));
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.i("scgfw", String.valueOf(latitude) + " " + String.valueOf(longitude));

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

        String provider_inet = "";
        boolean haveBestProvider = false;
        Criteria criteria = new Criteria();
        android.location.Location loc;


        try {

            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
                Log.i("Lintutude" , String.valueOf(latitude)) ;
//            }else if (isPassiveProvider){
//                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, Long.MIN_VALUE, Float.MAX_VALUE, locationListener, Looper.getMainLooper());
//                Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) ;
//                Log.i("IsPassive_1", "net") ;
//                Log.i("IsPassive_loc" ,String.valueOf(loc.getLatitude()) + " " + loc.getLongitude()) ;
            }else{

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 10, locationListener , Looper.getMainLooper());
            }

        } catch (NullPointerException ex) {

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

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(today);

        File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)));
        String name_pathToFile = path + "/" + name_file;

        System.out.println(name_pathToFile);

        getLocation() ;
        File f = new File(name_pathToFile);

        if(f.exists() && !f.isDirectory()) {

            List<LogsTerminal> detailsList = new ArrayList<>();
            detailsList.add(new LogsTerminal(formattedDate, String.valueOf(locationisOn), String.valueOf(latitude + " " + longitude)));
            writeCourseList(detailsList, String.valueOf(path), name_pathToFile) ;

        }else {

            if (longitude == 0.0 || latitude == 0.0 ){
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



//        List<LogsTerminal> detailsList = new ArrayList<>();
//        detailsList.add(new LogsTerminal(dateForLocation, String.valueOf(locationisOn), String.valueOf(longitude), String.valueOf(latitude)));
//
//        writeCourseList(detailsList, String.valueOf(path), name_pathToFile) ;




//        try (FileReader fileReader = new FileReader((name_pathToFile))){
//
//            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(fileReader) ;
//
//            ObjectMapper objectMapper = new ObjectMapper();


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                byte[] jsonData = Files.readAllBytes(Paths.get(path + File.separator + name_pathToFile));
//            }


//            JsonArray array = jsonObject.getAsJsonArray("Coordinates");
//
//            String name = String.valueOf(jsonObject.get("date"));
//            Log.i("sssss" ,name) ;
//
//            List<String> str = new ArrayList<>();
//
//
//            JsonObject newJspn = new JsonObject();
//            JsonArray arrName  = new JsonArray() ;
//            JsonArray aarValue = new JsonArray() ;
//            arrName.add("logitude");
//            arrName.add("lutude");
//
//            newJspn.add("Coordinates" ,arrName);
//            JSONObject js = new JSONObject() ;
//            JSONArray mass = new JSONArray();
//            mass.put(js);
//            JSONObject object= new JSONObject();
//            object.put("Coordinates" ,object) ;
//
//            List<Map<String ,String>> coordiantes =new ArrayList<>();
//            HashMap<String , String> addCordinates = new HashMap<>() ;
//
//            for (Object obj : array){
//
//                JsonObject jsonObject1 = (JsonObject) obj;
//                String logitude = String.valueOf(jsonObject1.get("logitude"));
//                String lutude = String.valueOf(jsonObject1.get("lutude"));
//
//                addCordinates.put(logitude ,lutude) ;
//                coordiantes.add(addCordinates) ;
//
//            }


//            for (Map<String, String> map : coordiantes ){
//
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//
//                    js.put("logitude" ,entry.getKey());
//                    js.put("logitude" ,entry.getValue());
//
//                    Log.i("valuets" , entry.getValue());
//                    Log.i("Keuy" , entry.getKey());
//
//
//                }
//            }
//
//
//            js.put("logitude" ,longitude) ;
//            js.put("logitude" ,latitude) ;



//            FileWriter fr = new FileWriter(name_pathToFile) ;
//            fr.write(newJspn.toString());
//            fr.flush();


//            Writer wr = new OutputStreamWriter(new FileOutputStream(new File(name_pathToFile),false));
//            wr.flush();
//            wr.write(String.valueOf(jsonObject));
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

//        File path = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)));
//    Writer wr = null;
//    //FileOutputStream fos = null;
     boolean isHaveInstanceProccesTSD = false ;
//
//    getLocation() ;
//
//    String path_file = path +"/"+ name_file ;
//    String dta = "" ;
//    JSONObject json = new JSONObject() ;
//    JSONArray arr = new JSONArray();
//    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//    JsonWriter js  ;


//    try {
//
////        arr.put(latitude) ;
////        arr.put(longitude) ;
////        json.put("Work is ",  dateForLocation) ;
////        json.put( "Coordinate ",  arr) ;
////        json.put("Location_ON" ,locationisOn) ;
////
////        dta = json.toString() ;
//
//
//    }catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    try {
//
////        name_file = new Date().toString() ;
//        path.mkdirs();
//        wr = new OutputStreamWriter(new FileOutputStream(new File(path, name_file),false));
//
//
////        Object obj = jsonParser.parse(reader);
////        JSONArray employeeList = (JSONArray) obj;
////        Log.i("xzcxcz" , String.valueOf(employeeList));
//
////        JSONArray employeeList1 = new JSONArray();
////
////        employeeList1.put(longitude) ;
////        employeeList1.put(latitude) ;
////
//
////
//        DataForJSON dataForJSON = new DataForJSON(new Date().toString(), "as" ,String.valueOf(latitude), String.valueOf(longitude)) ;
////
//              Gson gson1 = new Gson() ;
//            String locle = gson1.toJson(dataForJSON);
//        Log.i("ssss1123" ,locle) ;
////
//        JsonElement obj = JsonParser.parseReader(new FileReader(path +"/"+ name_file));
//
//        JsonObject jsonObject = (JsonObject) obj;
//
//        JsonArray msg = (JsonArray) jsonObject.get("shit");
//
//        System.out.println(jsonObject);
//        List<String> linststring  = new ArrayList<String>();
//
//
//        Iterator<JsonElement> iterator = msg.iterator();
//
//        while (iterator.hasNext()) {
//            String Vinno_Read = iterator.next().toString();
//            System.out.println("Vinno_Read---->" + Vinno_Read);
//        }

//
//        String name_path = path +"/"+ name_file ;
//        JsonElement obj = JsonParser.parseReader(new FileReader(name_path));
//        JsonArray jsonItemInfo = obj.getAsJsonArray();
//
//        JSONObject employeeDetails = new JSONObject();
//        JSONObject employeeObject = new JSONObject();
//        JSONArray employeeList = new JSONArray();
//        FileWriter file = new FileWriter(path +"/"+ name_file);
//
//
//        Log.i("path_file" ,path +"/"+ name_file) ;
//        employeeList.put(locle);
//
//        employeeDetails.put("shit", "xzcxczxczxcz") ;
//
//        file.write(employeeDetails.toString());
//        file.flush();
//        file.close();
//        FileWriter fileWriter = new FileWriter(String.valueOf(employeeDetails)) ;
//        fileWriter.flush();
//
//        File jsonFile = new File(path +"/"+ name_file);
//        Gson gson12 = new GsonBuilder()
//                .setPrettyPrinting()
//                .create();
//
//        Gson gson_new = new GsonBuilder().setPrettyPrinting().create();
//        Type type = new TypeToken<Map<String, Map.Entry>>() {}.getType();
//        Reader reader_new = new FileReader(name_path);
////        Map<String, Map.Entry> diary = gson_new.fromJson(reader_new, type);
//
//        JsonArray au = new JsonArray() ;
//        JsonObject jsOm = new JsonObject() ;
//
//        try (FileReader reader = new FileReader(jsonFile)) {
//            JsonObject root = gson12.fromJson(reader, JsonObject.class);
//
////            Log.i("sdsd", root.toString()) ;
//            Map<String, JsonElement> valuesToAdd = new LinkedHashMap<>();
//
//            // create fields iterator
//            Iterator<Map.Entry<String, JsonElement>> fieldsIterator = root.entrySet().iterator();
//            while (fieldsIterator.hasNext()) {
//
//                Map.Entry<String, JsonElement> entry = fieldsIterator.next();
//                // if entry represents array
//
//                if (entry.getValue().isJsonArray()) {
//                    // create wrapper object
//                    JsonObject arrayWrapper = new JsonObject();
//
//                    arrayWrapper.add(entry.getKey(), root.get(entry.getKey()));
//                    arrayWrapper.add("sad", root.get(entry.getKey()));
//
//
//                    valuesToAdd.put(entry.getKey(), arrayWrapper);
//
//                    // remove it from object.
//                    fieldsIterator.remove();
//                }
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                valuesToAdd.forEach((k, v) -> root.add(k + "List", v));
//            }
//
//            System.out.println(gson.toJson("asdasdas" + root));
//        }
//
//
//
//
//
//
//        //wr.write(employeeDetails.toString());
//        wr.flush();
//        wr.close();

//        RandomAccessFile randomAccessFile = new RandomAccessFile(path +"/"+ name_file, "rw");
//
//        long pos = randomAccessFile.length();
//        while (randomAccessFile.length() > 0) {
//            Log.i("asdasdas", "sdss");
//            pos--;
//            randomAccessFile.seek(pos);
//            if (randomAccessFile.readByte() == ']') {
//
//                randomAccessFile.seek(pos);
//                break;
//            }
//        }

//        String jsonElement = "{sds}";
//        randomAccessFile.writeBytes("," + jsonElement + "]");
//
//        randomAccessFile.close();



//        Map<String, String> mapa = new HashMap<>() ;
//        mapa.put("Time" ,new Date().toString());
//        mapa.put("Coordinate" ,String.valueOf(latitude));
//        mapa.put("Location_On" ,"Yes");
//
//
//
//        ArrayList<Double> coordinates = new ArrayList<>() ;
//        coordinates.add((double) latitude) ;
//        coordinates.add((double) longitude) ;
//
//        JSONObject logs = new JSONObject();
//
//
//        logs.put("Time", new Date());
//        logs.put("Coordinate", coordinates);
//        logs.put("Location_On" ,true) ;
//
//        JSONObject valuesObject = new JSONObject();
//        valuesObject.put("hun", logs) ;
//
//
//        JsonArray array = new JsonArray();
//        array.add(String.valueOf(logs));
//
//        FileWriter file = new FileWriter(path +"/"+ name_file);
//
//        file.write(logs.toString());
//        file.flush();
//        file.close();


//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//    } catch (IOException e) {
//        e.printStackTrace();
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
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
                Log.i("1mn" ,"y") ;

            File f = new File(fileName);
            byte[] buffer = new byte[(int)f.length()];
            FileInputStream is = new FileInputStream(fileName);
            is.read(buffer);
            is.close();
            jsonData = buffer ;
            details = objectMapper.readValue(jsonData, Details.class);

            List<LogsTerminal> existingCourseList = details.getDetailsList();
            if(null != existingCourseList && existingCourseList.size() > 0) {
                    Log.i("msd23" ,"sss") ;
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
