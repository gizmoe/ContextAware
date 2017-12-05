package com.maryland.cmsc436.contextaware;

import android.util.Log;

/**
 * Created by brianchung on 12/4/17.
 */

public class Fence {
    private String address;
    private String title;
    private double lat;
    private double lng;
    private double range;
    private boolean status;
    private ContextSettings.Ringer ringer;
    private final String TAG = "Context";

    public Fence(String address, String title, double lat, double lng, double range, ContextSettings.Ringer ringer){
        this.address = address;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.range = range;
        status = false;
        this.ringer = ringer;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAddress() {return this.address;}

    public double getlat(){
        return this.lat;
    }

    public double getLng(){
        return this.lng;
    }

    public double getRange(){
        return this.range;
    }

    public ContextSettings.Ringer getRinger(){
        return this.ringer;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean withinRange(double lat, double lng){
        double dist = distance(lat, lng, this.lat, this.lng, "K");
        Log.i(TAG, "Distance: " + dist);
        return dist <= this.range;
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
}
