package com.maryland.cmsc436.contextaware;

import android.content.ContentValues;
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
	private static final String SETTING_TITLE = "title";
	private static final String SETTING_RINGER = "ringer";	//0=SILENT, 1=VIBRATE, 2=RING
	private static final String SETTING_ACTIVE = "enabled";	//0=DISABLED, 1=ENABLED

	private static final String LOC_TABLE = "locations";
	private static final String LOC_ADDRESS = "address";
	private static final String LOC_LAT = "latitude";
	private static final String LOC_LONG = "longitude";
	private static final String LOC_SID = "sid";


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
		String createSettingTables = "CREATE TABLE IF NOT EXISTS " + SETTING_TABLE + " ("
				+ SETTING_TITLE + " TEXT NOT NULL, "
				+ SETTING_RINGER + " INTEGER, "
				+ SETTING_ACTIVE + " INTEGER "
				+ ");";
		String createLocTables = "CREATE TABLE IF NOT EXISTS" + LOC_TABLE + " ("
				+ LOC_ADDRESS + " TEXT NOT NULL,"
				+ LOC_LAT + " INTEGER, "
				+ LOC_LONG + " INTEGER, "
				+ LOC_SID + "INTEGER, "
				+ "PRIMARY KEY (" + LOC_LAT + ", " + LOC_LONG + ") "
				+ ");";

		db.execSQL(createLocTables);
		db.execSQL(createSettingTables);

	}
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE + ";");
		db.execSQL("DROP TABLE IF EXISTS " + LOC_TABLE + ";");

		onCreate(db);
	}


	//Information used to display in a list
	public ArrayList<ContextSettings> getAllSettings() {
		String[] cols = {SETTING_TITLE, SETTING_RINGER, SETTING_ACTIVE};
		ArrayList<ContextSettings> toReturn = new ArrayList<ContextSettings>();
		SQLiteDatabase db = getReadableDatabase();

		Cursor data = db.query(SETTING_TABLE, cols, null, null, null, null, SETTING_TITLE + " DESC");
		int listSize = data.getCount();
		data.moveToFirst();

		while (data.isAfterLast() != true) {
			String curTitle = data.getString(data.getColumnIndex(SETTING_TITLE));
			ContextSettings.ActiveStatus st;
			ContextSettings.Ringer ring;

			switch (data.getInt(data.getColumnIndex(SETTING_RINGER))) {
				case 0:
					ring = ContextSettings.Ringer.SILENT;
					break;
				case 1:
					ring = ContextSettings.Ringer.VIBRATE;
					break;
				default: //case 2:
					ring = ContextSettings.Ringer.LOUD;
					break;
			}
			switch (data.getInt(data.getColumnIndex(SETTING_ACTIVE))) {
				case 0:
					st = ContextSettings.ActiveStatus.NO;
					break;
				default: //case 1:
					st = ContextSettings.ActiveStatus.YES;
					break;
			}

			toReturn.add(new ContextSettings(curTitle, ring, st));
			data.moveToNext();
		}

        return toReturn;
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

    public void updateSetting(ContextSettings changedSettings) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues updates = new ContentValues();

		switch (changedSettings.getRinger()) {
			case SILENT:
				updates.put(SETTING_RINGER, 0);
				break;
			case VIBRATE:
				updates.put(SETTING_RINGER, 1);
				break;
			default: //case LOUD:
				updates.put(SETTING_RINGER, 2);
				break;
		}

		switch (changedSettings.getStatus()) {
			case YES:
				updates.put(SETTING_ACTIVE, 1);
				break;
			default: //case NO:
				updates.put(SETTING_ACTIVE, 0);
				break;
		}

		db.update(SETTING_TABLE, updates, SETTING_TITLE + " = ?", new String[] { changedSettings.getTitle() });
    }

    public void updatePlace() {
		String locUpdate = "";

    }

}
