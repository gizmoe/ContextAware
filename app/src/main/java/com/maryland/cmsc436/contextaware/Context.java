package com.maryland.cmsc436.contextaware;

/**
 * Created by brianchung on 12/4/17.
 */

public class Context {
    private String id;
    private double lat;
    private double lng;
    private double range;
    private boolean status;

    public Context(String id, double lat, double lng, double range){
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.range = range;
        status = false;
    }


    public String getID(){
        return this.id;
    }

    public double getlat(){
        return this.lat;
    }

    public double getLng(){
        return this.lng;
    }

    public double getRange(){
        return this.range;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }


}
