package com.bananalab.tracking.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.bananalab.tracking.model.Coordinate;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by batmaster on 5/10/16 AD.
 */
public class LocationBackgroundService extends Service {

    private GoogleApiClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("DBH Service", "onCreate");

        final LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("DBH Service", "onLocationChanged");
                DBHelper.track(getApplicationContext(), new Coordinate(Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP), location.getLatitude(), location.getLongitude(), location.getAltitude()));
            }
        };

        GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Log.d("DBH Service", "onConnected");

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Cannot start tracking because location permission is not granted.", Toast.LENGTH_SHORT).show();
                    Log.d("DBH Service", "return false");
                    return;
                }
                Location location = LocationServices.FusedLocationApi.getLastLocation(client);

                LocationRequest request = new LocationRequest();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setInterval(1000);
                request.setSmallestDisplacement(5);

                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, locationListener);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client.connect();
        Log.d("DBH Service", "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("DBH Service", "onDestroy");
        client.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
