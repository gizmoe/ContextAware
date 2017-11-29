package com.maryland.cmsc436.contextaware;

import android.content.Intent;

public class Item {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Ringer {
        SILENT, VIBRATE, LOUD
    };

    public enum ActiveStatus {
        YES, NO
    };

    public final static String TITLE = "title";
    public final static String RINGER = "ringer";
    public final static String ACTIVESTATUS = "status";

    // set the default title, ringer setting and status
    private String title = new String();
    private ContextSettings.Ringer ringer = ContextSettings.Ringer.VIBRATE;
    private ContextSettings.ActiveStatus status = ContextSettings.ActiveStatus.YES;

    Item(String title, ContextSettings.Ringer ringer, ContextSettings.ActiveStatus status) {
        this.title = title;
        this.ringer = ringer;
        this.status = status;
    }

    // Create a new Context from data packaged in an Intent
    Item(Intent intent) {
        title = intent.getStringExtra(ContextSettings.TITLE);
        ringer = ContextSettings.Ringer.valueOf(intent.getStringExtra(ContextSettings.RINGER));
        status = ContextSettings.ActiveStatus.valueOf(intent.getStringExtra(ContextSettings.ACTIVESTATUS));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public ContextSettings.Ringer getRinger() {
        return ringer;
    }

    public void setRinger(ContextSettings.Ringer newRinger) {
        ringer = newRinger;
    }

    public ContextSettings.ActiveStatus getStatus() {
        return status;
    }

    public void setStatus(ContextSettings.ActiveStatus newStatus) {
        status = newStatus;
    }

    // Take a set of String data values and
    // package them for transport in an Intent
    public static void packageIntent(Intent intent, String title,
                                     ContextSettings.Ringer ringer, ContextSettings.ActiveStatus status) {
        intent.putExtra(ContextSettings.TITLE, title);
        intent.putExtra(ContextSettings.RINGER, ringer.toString());
        intent.putExtra(ContextSettings.ACTIVESTATUS, status.toString());
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
