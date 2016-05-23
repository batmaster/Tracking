package com.bananalab.tracking.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bananalab.tracking.model.Coordinate;
import com.bananalab.tracking.model.Tracking;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by batmaster on 5/2/16 AD.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tracking";

    private Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE trackings (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, date TEXT, distance REAL, elapse INTEGER, size INTEGER, hasSync INTEGER)"));
        db.execSQL(String.format("CREATE TABLE coordinates (id INTEGER PRIMARY KEY AUTOINCREMENT, t_id INTEGER, date TEXT, latitude REAL, longitude REAL, altitude REAL, hasSync INTEGER)"));
    }

//    // for testing
//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//
////        db.execSQL("ALTER TABLE trackings ADD COLUMN coordinates INTEGER");
//
////
//        db.execSQL("DROP TABLE trackings");
//        db.execSQL("DROP TABLE coordinates");
//        onCreate(db);
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static void startTracking(Context context) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", "recording...");
        values.put("date", Preferences.SDF.format(new Date()));
        values.put("elapse", -1);
        values.put("distance", -1);

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

            Intent intent = new Intent(TrackingApplication.INTENT_FILTER_NOTIFY_MAP);
            intent.putExtra("newCoordinate", (Serializable) coordinate);
            context.sendBroadcast(intent);

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
        } catch (ArrayIndexOutOfBoundsException e) {
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
        values.put("title", tracking.getTitle().trim());
        values.put("description", tracking.getDescription().trim());
        values.put("elapse", tracking.getElapse());
        values.put("distance", tracking.getDistance());
        values.put("size", coordinates.size());

        db.update("trackings", values, "id = ?", new String[] {Integer.toString(trackingTempId)});

        Preferences.removeInt(context, Preferences.TRACKING_ID_TEMP);

        Log.d("DBH", "finishTracking " + values.toString());

        FireBaseHelper.saveTracking(context, trackingTempId, 0);
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
        return getTrackings(context, "");
    }

    public static ArrayList<Tracking> getTrackings(Context context, String query) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getReadableDatabase();

        ArrayList<Tracking> trackings = new ArrayList<Tracking>();

        if (query == null)
            query = "";

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM trackings WHERE title LIKE '%%%1$s%%' OR description LIKE '%%%1$s%%' ORDER BY id DESC", query), null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            trackings.add(new Tracking(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getLong(5), cursor.getInt(6), cursor.getInt(7)));
            cursor.moveToNext();
        }

        Log.d("DBH", "getTrackings " + trackings.size());

        return trackings;
    }

    public static Tracking getTracking(Context context, int t_id) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getReadableDatabase();

        Tracking tracking = null;

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM trackings WHERE id = %d ORDER BY id DESC", t_id), null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            tracking = new Tracking(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getLong(5), cursor.getInt(6), cursor.getInt(7));
            cursor.moveToNext();
        }

        Log.d("DBH", "getTracking " + tracking);

        return tracking;
    }

    public static void setHasSync(Context context, int t_id, int hasSync, int inListPosition) {
        DBHelper that = new DBHelper(context);
        SQLiteDatabase db = that.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("hasSync", 1);

        db.update("trackings", values, "id = ?", new String[] {Integer.toString(t_id)});

        Intent intent = new Intent(TrackingApplication.INTENT_FILTER_REFRESH_LIST);
        intent.putExtra("inListPosition", inListPosition);
        context.sendBroadcast(intent);

        Log.d("DBH", "setHasSync " + t_id + " to " + hasSync);
    }

}
