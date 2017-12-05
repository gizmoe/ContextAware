package com.maryland.cmsc436.contextaware;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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

public class ContextManager {

    private final String TAG = "Context";
    private LocationManager locationManager;
    private LocationListener listener;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private GeoApiContext context;
    private GoogleApiClient mGoogleApiClient;

    private List<Fence> contexts = new ArrayList<Fence>();
    private Activity mActivity;
    private Context mContext;
    private DBAccess db;

    public ContextManager(Activity mActivity, DBAccess db){
        this.mActivity = mActivity;
        this.mContext = mActivity.getApplicationContext();
        this.db = db;
        configureGoogleAPIs();
        configureLocationListener();
        context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAe9ppAcukWJ9qTTWEIX_OMLUkXd1UGQfw")
                .build();
    }

    private void configureGoogleAPIs(){
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void configureLocationListener() {
        locationManager = (LocationManager) this.mContext.getSystemService(mContext.LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
                for (Fence c : contexts) {
                    boolean inRange = c.withinRange(location.getLatitude(), location.getLongitude());

                    if (inRange && !c.getStatus()) {
                        AudioManager audioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
                        switch(c.getRinger()){
                            case LOUD:
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                break;
                            case SILENT:
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                break;
                            case VIBRATE:
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                break;
                        }

                        Log.i(TAG, "In Location: " + c.getAddress());
                        c.setStatus(true);
                    }

                    if (!inRange && c.getStatus()) {
                        Log.i(TAG, "Exiting Location: " + c.getAddress());
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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        }
    }


    public void createContext(String title, String address, ContextSettings.Ringer ringer){
        double lat = 0.0;
        double lng = 0.0;
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.i(TAG, gson.toJson(results[0].geometry.location));
            lat = results[0].geometry.location.lat;
            lng = results[0].geometry.location.lng;
        } catch (Exception e) {
            Log.i(TAG, "Failed to get lat/lng of address " + e.getMessage());
            return;
        }

        Fence newFence = new Fence(address, title, lat,lng, 0.05, ringer);
        contexts.add(newFence);
        Log.i(TAG, "Created New Fence");
    }



}