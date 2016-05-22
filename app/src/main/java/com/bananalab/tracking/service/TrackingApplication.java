package com.bananalab.tracking.service;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.firebase.client.Firebase;

/**
 * Created by batmaster on 5/22/16 AD.
 */
public class TrackingApplication extends Application {

    public static final String INTENT_FILTER_NOTIFY_MAP = "com.bananalab.tracking.INTENT_FILTER_NOTIFY_MAP";
    public static final String INTENT_FILTER_REFRESH_LIST = "com.bananalab.tracking.INTENT_FILTER_REFRESH_LIST";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
