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
    public final static String RINGER = "ringer";
    public final static String ACTIVESTATUS = "status";

    // set the default title, ringer setting and status
    private String title = new String();
    private Ringer ringer = Ringer.VIBRATE;
    private ActiveStatus status = ActiveStatus.YES;

    ContextSettings(String title, Ringer ringer, ActiveStatus status) {
        this.title = title;
        this.ringer = ringer;
        this.status = status;
    }


    ContextSettings(String title, String ringer, String status) {
        this.title = title;
        ringer = ringer.toLowerCase();
        if (ringer.equals("silent"))
            this.ringer = ContextSettings.Ringer.SILENT;
        else if (ringer.equals("vibrate"))
            this.ringer = ContextSettings.Ringer.VIBRATE;
        else
            this.ringer = ContextSettings.Ringer.LOUD;

        status = status.toLowerCase();
        if (status.equals("yes"))
            this.status = ContextSettings.ActiveStatus.YES;
        else
            this.status = ContextSettings.ActiveStatus.NO;
    }


    // Create a new Context from data packaged in an Intent
    ContextSettings(Intent intent) {
        title = intent.getStringExtra(ContextSettings.TITLE);
        ringer = Ringer.valueOf(intent.getStringExtra(ContextSettings.RINGER));
        status = ActiveStatus.valueOf(intent.getStringExtra(ContextSettings.ACTIVESTATUS));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public Ringer getRinger() {
        return ringer;
    }

    public void setRinger(String newRinger) {
        if (newRinger != null) {
            newRinger = newRinger.toLowerCase();
            if (newRinger.equals("silent"))
                ringer = ContextSettings.Ringer.SILENT;
            else if (newRinger.equals("vibrate"))
                ringer = ContextSettings.Ringer.VIBRATE;
            else
                ringer = ContextSettings.Ringer.LOUD;
        } else
            ringer = ContextSettings.Ringer.SILENT;
    }

    public ActiveStatus getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        if (newStatus != null) {
            newStatus = newStatus.toLowerCase();
            if (newStatus.equals("yes"))
                status = ContextSettings.ActiveStatus.YES;
            else
                status = ContextSettings.ActiveStatus.NO;
        } else
            status = ContextSettings.ActiveStatus.NO;
    }

    // Take a set of String data values and
    // package them for transport in an Intent
    public static void packageIntent(Intent intent, String title,
                                     Ringer ringer, ActiveStatus status, Integer pos) {
        intent.putExtra(ContextSettings.TITLE, title);
        intent.putExtra(ContextSettings.RINGER, ringer.toString());
        intent.putExtra(ContextSettings.ACTIVESTATUS, status.toString());
        intent.putExtra("pos", pos);
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