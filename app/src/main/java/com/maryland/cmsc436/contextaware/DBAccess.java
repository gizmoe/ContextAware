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
 * Current issues: supports having identical names, but currently can only edit one
 */

public class DBAccess extends SQLiteOpenHelper {
	private static DBAccess singleton;

	private static final String DB_NAME = "ContextAwareDB";
	private static final int DB_VERSION = 1;

	private static final String SETTING_TABLE = "settings";
	private static final String SETTING_KEY = "sid";
	private static final String SETTING_TITLE = "title";
	private static final String SETTING_RINGER = "ringer";	//0=SILENT, 1=VIBRATE, 2=RING
	private static final String SETTING_ACTIVE = "enabled";	//0=DISABLED, 1=ENABLED
	private static final String SETTING_LOC = "location";

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
				+ SETTING_KEY + " INTEGER PRIMARY KEY, "
				+ SETTING_TITLE + " TEXT NOT NULL, "
				+ SETTING_RINGER + " INTEGER, "
				+ SETTING_ACTIVE + " INTEGER, "
				+ SETTING_LOC + " TEXT"
				+ ");";
//		String createLocTables = "CREATE TABLE IF NOT EXISTS" + LOC_TABLE + " ("
//				+ LOC_ADDRESS + " TEXT NOT NULL,"
//				+ LOC_LAT + " INTEGER, "
//				+ LOC_LONG + " INTEGER, "
//				+ LOC_SID + "INTEGER, "
//				+ "PRIMARY KEY (" + LOC_LAT + ", " + LOC_LONG + ") "
//				+ ");";

		//db.execSQL(createLocTables);
		db.execSQL(createSettingTables);

	}
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE + ";");
		db.execSQL("DROP TABLE IF EXISTS " + LOC_TABLE + ";");

		onCreate(db);
	}


	//Information used to display in a list
	public ArrayList<ContextSettings> getAllSettings() {
		String[] cols = {SETTING_TITLE, SETTING_RINGER, SETTING_ACTIVE, SETTING_LOC};
		ArrayList<ContextSettings> toReturn = new ArrayList<ContextSettings>();
		SQLiteDatabase db = getReadableDatabase();

		Cursor data = db.query(SETTING_TABLE, cols, null, null, null, null, SETTING_TITLE + " DESC");
		int listSize = data.getCount();
		data.moveToFirst();

		while (data.isAfterLast() != true) {
			String curTitle = data.getString(data.getColumnIndex(SETTING_TITLE));
			String loc = data.getString(data.getColumnIndex(SETTING_LOC));
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

			toReturn.add(new ContextSettings( curTitle, ring, loc, st ));
			data.moveToNext();
		}

        return toReturn;
	}

	//All details on one setting
	public void putNewSetting(ContextSettings newContext) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues vals = new ContentValues();

		switch (newContext.getRinger()) {
			case SILENT:
				vals.put(SETTING_RINGER, 0);
				break;
			case VIBRATE:
				vals.put(SETTING_RINGER, 1);
				break;
			default: //case LOUD:
				vals.put(SETTING_RINGER, 2);
				break;
		}

		switch (newContext.getStatus()) {
			case YES:
				vals.put(SETTING_ACTIVE, 1);
				break;
			default: //case NO:
				vals.put(SETTING_ACTIVE, 0);
				break;
		}

		vals.put(SETTING_LOC,newContext.getLocation());
		vals.put(SETTING_TITLE, newContext.getTitle());

		db.insert(SETTING_TABLE, null, vals);

	}

	public void removeSetting(ContextSettings s) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(SETTING_TABLE, SETTING_TITLE + " = ?",new String[]{s.getTitle()});
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

		updates.put(SETTING_LOC,changedSettings.getLocation());

		db.update(SETTING_TABLE, updates, SETTING_TITLE + " = ?", new String[] { changedSettings.getTitle() });
    }

    public ContextSettings getByLocation(String loc) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor data = db.query(SETTING_TABLE
				, new String[]{SETTING_TITLE,SETTING_ACTIVE,SETTING_RINGER}
				, SETTING_LOC + " = ?"
				, new String[]{loc}
				, null, null, null);

		if (data.moveToFirst()) {
			ContextSettings.ActiveStatus stat;
			ContextSettings.Ringer ringer;

			String title = data.getString(data.getColumnIndex(SETTING_TITLE));

			switch ( data.getInt(data.getColumnIndex(SETTING_ACTIVE)) ) {
				case 1:
					stat = ContextSettings.ActiveStatus.YES;
					break;
				default:
					stat = ContextSettings.ActiveStatus.NO;
					break;
			}

			switch ( data.getInt(data.getColumnIndex(SETTING_RINGER)) ) {
				case 0:
					ringer = ContextSettings.Ringer.SILENT;
					break;
				case 1:
					ringer = ContextSettings.Ringer.VIBRATE;
					break;
				default:
					ringer = ContextSettings.Ringer.LOUD;
					break;
			}

			
			return new ContextSettings(title, ringer, loc, stat);
		} else {
			return null;
		}

	}

}
