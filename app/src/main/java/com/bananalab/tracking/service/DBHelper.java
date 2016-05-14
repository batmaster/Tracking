package com.bananalab.tracking.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.bananalab.tracking.model.Coordinate;
import com.bananalab.tracking.model.Tracking;
import com.bananalab.tracking.view.NotifiableMapActivity;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by batmaster on 5/2/16 AD.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tracking";

    private Context context;

    private static NotifiableMapActivity notifiableMapActivity;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE trackings (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, date TEXT, distance REAL, elapse TEXT)"));
        db.execSQL(String.format("CREATE TABLE coordinates (id INTEGER PRIMARY KEY AUTOINCREMENT, t_id INTEGER, date TEXT, latitude REAL, longitude REAL, altitude REAL)"));
    }

//    // for testing
//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//        db.execSQL("DROP TABLE trackings");
//        db.execSQL("DROP TABLE coordinates");
//        onCreate(db);
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static void setNotifiableMapActivity(NotifiableMapActivity activity) {
        notifiableMapActivity = activity;
        Log.d("DBH", "setNotifiableMapActivity " + activity);
    }

    public static void removeNotifiableMapActivity() {
        notifiableMapActivity = null;

        Log.d("DBH", "removeNotifiableMapActivity");
    }

    public static void startTracking(Context context) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", "recording...");

        int id = (int) db.insert("trackings", null, values);

        Preferences.setInt(context, Preferences.TRACKING_ID_TEMP, id);

        Log.d("DBH", "startTracking " + id + " " + values.toString());
    }

    public static void track(Context context, Coordinate coordinate) {
        if (Preferences.getInt(context, Preferences.TRACKING_ID_TEMP) != -1) {
            DBHelper that = new DBHelper(context);
            SQLiteDatabase db = that.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("t_id", coordinate.getT_id());
            values.put("date", coordinate.getDate());
            values.put("latitude", coordinate.getLatitude());
            values.put("longitude", coordinate.getLongitude());
            values.put("altitude", coordinate.getAltitude());

            long id = db.insert("coordinates", null, values);

            if (notifiableMapActivity != null) {
                notifiableMapActivity.notifyMap(coordinate);
            }

            Log.d("DBH", "track " + values.toString());
        }
    }

    /**
     * Tracking with title, description, date
     * @param context
     * @param tracking
     */
    public static void finishTracking(Context context, Tracking tracking) {
        DBHelper that = new DBHelper(context);



        SQLiteDatabase db = that.getWritableDatabase();

        int trackingTempId = Preferences.getInt(context, Preferences.TRACKING_ID_TEMP);


        ArrayList<Coordinate> coordinates = DBHelper.getCoordinates(context, trackingTempId);

        long elapse = 0;
        try {
            elapse = Preferences.SDF.parse(coordinates.get(coordinates.size() - 1).getDate()).getTime() - Preferences.SDF.parse(coordinates.get(0).getDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tracking.setElapse(elapse);

        Coordinate prevLoc = null;
        double distance = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            if (prevLoc != null) {
                float[] res = new float[1];
                Location.distanceBetween(prevLoc.getLatitude(), prevLoc.getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude(), res);
                distance += res[0];
            }

            prevLoc = coordinates.get(i);
        }
        tracking.setDistance(distance);

        ContentValues values = new ContentValues();
        values.put("title", tracking.getTitle());
        values.put("description", tracking.getDescription());
        values.put("elapse", tracking.getElapse());
        values.put("date", tracking.getDate());
        values.put("distance", tracking.getDistance());

        db.update("trackings", values, "id = ?", new String[] {Integer.toString(trackingTempId)});

        Preferences.removeInt(context, Preferences.TRACKING_ID_TEMP);
        removeNotifiableMapActivity();

        Log.d("DBH", "finishTracking " + values.toString());
    }

    public static ArrayList<Coordinate> getCoordinates(Context context, int t_id) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getReadableDatabase();

        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM coordinates WHERE t_id = %d", t_id), null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            coordinates.add(new Coordinate(cursor.getInt(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5)));
            cursor.moveToNext();
        }

        Log.d("DBH", "getCoordinates " + coordinates.size());

        return coordinates;
    }

    public static ArrayList<Tracking> getTrackings(Context context) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getReadableDatabase();

        ArrayList<Tracking> trackings = new ArrayList<Tracking>();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM trackings ORDER BY id DESC"), null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            trackings.add(new Tracking(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getLong(5)));
            cursor.moveToNext();
        }

        Log.d("DBH", "getTrackings " + trackings.size());

        return trackings;
    }

}
