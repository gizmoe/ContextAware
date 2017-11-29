package com.maryland.cmsc436.contextaware;

import android.content.Intent;

public class ContextSettings {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Ringer {
        SILENT, VIBRATE, LOUD
    };

    public enum ActiveStatus {
        YES, NO
    };

    public final static String TITLE = "title";
    public final static String LOCATION = "location";
    public final static String RINGER = "ringer";
    public final static String ACTIVESTATUS = "status";

    // set the default title, ringer setting and status
    private String title = new String();
    private String location = new String();
    private Ringer ringer = Ringer.VIBRATE;
    private ActiveStatus status = ActiveStatus.YES;

    ContextSettings(String title, Ringer ringer, String location, ActiveStatus status) {
        this.title = title;
        this.ringer = ringer;
        this.location = location;
        this.status = status;
    }

    // Create a new Context from data packaged in an Intent
    ContextSettings(Intent intent) {
        title = intent.getStringExtra(ContextSettings.TITLE);
        ringer = Ringer.valueOf(intent.getStringExtra(ContextSettings.RINGER));
        location = intent.getStringExtra(ContextSettings.LOCATION);
        status = ActiveStatus.valueOf(intent.getStringExtra(ContextSettings.ACTIVESTATUS));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String newLocation) {
        location = newLocation;
    }

    public Ringer getRinger() {
        return ringer;
    }

    public void setRinger(Ringer newRinger) {
        ringer = newRinger;
    }

    public ActiveStatus getStatus() {
        return status;
    }

    public void setStatus(ActiveStatus newStatus) {
        status = newStatus;
    }

    // Take a set of String data values and
    // package them for transport in an Intent
    public static void packageIntent(Intent intent, String title,
                                     Ringer ringer, String location, ActiveStatus status) {
        intent.putExtra(ContextSettings.TITLE, title);
        intent.putExtra(ContextSettings.RINGER, ringer.toString());
        intent.putExtra(ContextSettings.ACTIVESTATUS, status.toString());
        intent.putExtra(ContextSettings.LOCATION,location);
    }

    // Here I will create my own toString method
    public String toString() {
        return title + ITEM_SEP + ringer + ITEM_SEP + status + ITEM_SEP;
    }

    public String toLog() {
        return "Title:" + title + ITEM_SEP + "Ringer:" + ringer
                + ITEM_SEP + "Active Status:" + status + "\n";
    }
}
