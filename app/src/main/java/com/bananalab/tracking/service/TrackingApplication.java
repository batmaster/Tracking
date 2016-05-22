package com.bananalab.tracking.service;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by batmaster on 5/22/16 AD.
 */
public class TrackingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
