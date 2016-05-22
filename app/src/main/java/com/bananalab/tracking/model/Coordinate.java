package com.bananalab.tracking.model;

import android.util.Log;

import com.bananalab.tracking.service.Preferences;

import java.util.Date;

/**
 * Created by batmaster on 5/2/16 AD.
 */
public class Coordinate {

    private int t_id;
    private String date;
    private double latitude;
    private double longitude;
    private double altitude;
    private int hasSync;

    /**
     * When query from db.
     * @param t_id
     * @param date
     * @param latitude
     * @param longitude
     * @param altitude
     * @param hasSync
     */

    public Coordinate(int t_id, String date, double latitude, double longitude, double altitude, int hasSync) {
        this.t_id = t_id;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.hasSync = hasSync;
    }

    /**
     * When create in app.
     * @param t_id
     * @param latitude
     * @param longitude
     * @param altitude
     */
    public Coordinate(int t_id, double latitude, double longitude, double altitude) {
        this.t_id = t_id;
        date = Preferences.SDF.format(new Date());
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }



    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getHasSync() {
        return hasSync;
    }

    public void setHasSync(int hasSync) {
        this.hasSync = hasSync;
    }
}
