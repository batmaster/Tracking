package com.bananalab.tracking.model;

/**
 * Created by batmaster on 4/24/16 AD.
 */
public class Tracking {

    private String title;
    private String session;
    private String datetime;
    private String distance;

    public Tracking(String title, String session, String datetime, String distance) {

        this.title = title;
        this.session = session;
        this.datetime = datetime;
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
