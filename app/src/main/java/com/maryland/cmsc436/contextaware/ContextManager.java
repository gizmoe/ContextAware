package com.maryland.cmsc436.contextaware;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brianchung on 11/29/17.
 */

public class ContextManager extends AppCompatActivity {

    private final String TAG = "Context";
    private LocationManager locationManager;
    private LocationListener listener;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private GoogleApiClient mGoogleApiClient;
    private GeoApiContext context;


    private List<Context> contexts = new ArrayList<Context>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.context);
        configureLocationListener();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAe9ppAcukWJ9qTTWEIX_OMLUkXd1UGQfw")
                .build();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                   Log.i(TAG, "Permission not Granted");
                }
            }
        }
    }

    private void registerLocationFence(String strAddress){
        double lat = 0.0;
        double lng = 0.0;
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context,strAddress).await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.i(TAG, gson.toJson(results[0].geometry.location));
            lat = results[0].geometry.location.lat;
            lng = results[0].geometry.location.lng;
        } catch (Exception e) {
            Log.i(TAG, "Failed to get lat/lng of address " + e.getMessage());
            return;
        }

        Context newContext = new Context(strAddress, lat,lng, 100.0);
        contexts.add(newContext);
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private void configureLocationListener(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG,"Location: " + location.getLatitude() + " " + location.getLongitude());

                for(Context c: contexts){
                    double lat = c.getlat();
                    double lng = c.getLng();

                    double dist = distance(lat, lng, location.getLatitude(), location.getLongitude(), "K");
                    Log.i(TAG, "Distance is: " + dist + "kilometers");
                    if(dist <= c.getRange() && !c.getStatus()){
                        Log.i(TAG, "In Location: " + c.getID());
                        Toast.makeText(getApplicationContext(), "In Location: " + c.getID(), Toast.LENGTH_LONG);
                        c.setStatus(true);
                    }

                    if(dist > c.getRange() && c.getStatus()) {
                        Log.i(TAG, "Exiting Location: " + c.getID());
                        Toast.makeText(getApplicationContext(), "Exiting Location: " + c.getID(), Toast.LENGTH_LONG);
                        c.setStatus(false);
                    }
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }


    public void getLocation(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            registerLocationFence("William E. Kirwan Hall, 4176 Campus Dr, College Park, MD 20742");
        }
    }


}