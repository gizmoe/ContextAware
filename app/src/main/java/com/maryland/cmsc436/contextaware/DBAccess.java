package com.maryland.cmsc436.contextaware;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by kevin on 11/20/17.
 * This class will wrap calls to sqlite database to simplify database usage
 */

public class DBAccess {
	private static DBAccess singleton;
	//TODO: general implementation, and parameters for methods

	//Factory method to get the object
	public static DBAccess getInstance(Context context) {
		if (singleton == null) {
			singleton = new DBAccess(context);
		}
		return singleton;
	}

	private DBAccess(Context context) {

	}

	public void close() {

	}

	//Information used to display in a list
	public ArrayList getAllSettings() {
        return null;
	}

	//Information to display in a list
	public ArrayList getAllPlaces() {

        return null;
	}

	//All details on one setting
	public Object getSettingAt() {
		return null;
	}

	//All details on one "place"
	public Object getPlaceAt() {
        return null;
    }

    public void updateSetting() {

    }

    public void updatePlace() {

    }

}
