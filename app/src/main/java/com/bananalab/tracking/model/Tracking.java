package com.bananalab.tracking.model;

import com.bananalab.tracking.service.Preferences;

import java.util.Date;

/**
 * Created by batmaster on 4/24/16 AD.
 */
public class Tracking {

    private int id;
    private String title;
    private String description;
    private long elapse;
    private String date;
    private double distance;
    private int size;

    /**
     * When query from db.
     * @param title
     * @param description
     * @param elapse
     * @param date
     * @param distance
     * @param size
     */
    public Tracking(int id, String title, String description, String date, double distance, long elapse, int size) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.distance = distance;
        this.elapse = elapse;
        this.size = size;
    }

    /**
     * When create in app.
     * @param title
     * @param description
     */
    public Tracking(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return  description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getElapse() {
        return elapse;
    }

    public void setElapse(long elapse) {
        this.elapse = elapse;
    }

    public String getElapseString() {
        if (distance == -1)
            return "";

        long sec = elapse / 1000;
        if (sec < 60)
            return sec + " วินาที";
        else if (sec < 3600) {
            String str = "";
            str += (sec / 60) + " นาที";
            sec %= 60;

            if (sec != 0)
                str += " " + sec + " วินาที";

            return str;
        }
        else {
            String str = "";
            str += (sec / 3600) + " ชั่วโมง";
            sec %= 3600;

            if (sec != 0)
                str += " " + (sec / 60) + " นาที";

            return str;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public String getDistanceString() {
        if (distance == -1)
            return "";

        double d = distance;
        if (d < 1000)
            return String.format("%.1f", d) + " เมตร";
        else {
            String str = "";
            str += String.format("%.0f กิโลเมตร", d / 1000);
            d %= 1000;

            if (d != 0)
                str += String.format(" %.0f เมตร", d);

            return str;
        }
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
