package com.maryland.cmsc436.contextaware;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by kevin on 11/20/17.
 * This class will wrap calls to sqlite database to simplify database usage
 * 
 */

public class DBAccess extends SQLiteOpenHelper {
	private static DBAccess singleton;

	private static final String DB_NAME = "ContextAwareDB";
	private static final int DB_VERSION = 1;

	private static final String SETTING_TABLE = "settings";

	private static final String LOC_TABLE = "locations";


	//Factory method to get the object
	public static DBAccess getInstance(Context context) {
		if (singleton == null) {
			singleton = new DBAccess(context);
		}
		return singleton;
	}

	private DBAccess(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String createLocTables = "";
		String createSettingTables = "";
		//TODO

		db.execSQL(createLocTables);
		db.execSQL(createSettingTables);

	}
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOC_TABLE);

		onCreate(db);
	}


	//Information used to display in a list
	public ArrayList getAllSettings() {
		String settingSelect = "";
		ArrayList toReturn = new ArrayList();
		SQLiteDatabase db = getReadableDatabase();
		//TODO

		//Cursor data = db.query(SETTING_TABLE,String[] cols, sel, selargs, groupby, having, orderby)
		//int listSize = data.getCount();
		//data.moveToFirst();
		//while (data.isLast() != true) {
		//data.moveToNext();

        return null;
	}

	//Information to display in a list
	public ArrayList getAllPlaces() {
		String locSelect = "";

        return null;
	}

	//All details on one setting
	public Object getSettingAt() {
		String oneSettingSelect = "";

		return null;
	}

	//All details on one "place"
	public Object getPlaceAt() {
		String onePlaceSelect = "";

        return null;
    }

    public void updateSetting() {
		String settingUpdate = "";

    }

    public void updatePlace() {
		String locUpdate = "";

    }

}
