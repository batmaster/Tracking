package com.bananalab.tracking.view;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Coordinate;
import com.bananalab.tracking.service.DBHelper;
import com.bananalab.tracking.service.Preferences;
import com.bananalab.tracking.service.TrackingApplication;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.ParseException;
import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private int t_id;
    private GoogleMap googleMap;

    private Button buttonStop;

    private Coordinate prevLoc;
    private boolean firstZoom = true;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
            }
        });

        t_id = getIntent().getIntExtra("t_id", -1);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Cannot open map because location permission is not granted.", Toast.LENGTH_SHORT).show();

            finish();
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Coordinate newCoordinate = (Coordinate) intent.getExtras().getSerializable("newCoordinate");
                Log.d("DBH map", "notifyMap " + newCoordinate);

                if (prevLoc != null) {
                    LatLng prevLatLng = new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude());
                    LatLng curLatLng = new LatLng(newCoordinate.getLatitude(), newCoordinate.getLongitude());

                    float[] res = new float[1];
                    Location.distanceBetween(prevLatLng.latitude, prevLatLng.longitude, curLatLng.latitude, curLatLng.longitude, res);

                    long elapseHr = 0;
                    try {
                        elapseHr = Preferences.SDF.parse(newCoordinate.getDate()).getTime() - Preferences.SDF.parse(prevLoc.getDate()).getTime();
                        elapseHr /= 3600000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    float velocity = res[0] / 1000 / elapseHr;
                    Log.d("km/hr", velocity + "");

                    googleMap.addPolyline(new PolylineOptions()
                            .add(prevLatLng, curLatLng)
                            .width(6)
                            .color(velocity > 80 ? Color.RED : (velocity > 40 ? Color.BLUE : Color.GREEN))
                            .visible(true)
                    );

                    if (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(curLatLng)) {
                        if (firstZoom) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 15));
                        }
                        else {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, googleMap.getCameraPosition().zoom));
                        }
                    }
                }


                prevLoc = newCoordinate;
            }
        };
        if (t_id == Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP)) {
            registerReceiver(broadcastReceiver, new IntentFilter(TrackingApplication.INTENT_FILTER_NOTIFY_MAP));
        }

        ShowPolyLine task = new ShowPolyLine();
        task.execute();


    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonStop.setVisibility(Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP) == -1 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private class ShowPolyLine extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ArrayList<Coordinate> coordinates = DBHelper.getCoordinates(getApplicationContext(), t_id);
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < coordinates.size(); i++) {
                LatLng curLatLng = new LatLng(coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude());

                if (prevLoc != null) {
                    LatLng prevLatLng = new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude());

                    float[] res = new float[1];
                    Location.distanceBetween(prevLatLng.latitude, prevLatLng.longitude, curLatLng.latitude, curLatLng.longitude, res);

                    float elapseHr = 0;
                    try {
                        elapseHr = Preferences.SDF.parse(coordinates.get(i).getDate()).getTime() - Preferences.SDF.parse(prevLoc.getDate()).getTime();
                        elapseHr /= 3600000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    float velocity = res[0] / 1000 / elapseHr;


                    googleMap.addPolyline(new PolylineOptions()
                            .add(prevLatLng, curLatLng)
                            .width(6)
                            .color(velocity > 80 ? Color.RED : (velocity > 40 ? Color.BLUE : Color.GREEN))
                            .visible(true)
                    );
                }
                prevLoc = coordinates.get(i);
                builder.include(curLatLng);
            }

            Log.d("DBH map", "onMapReady " + t_id + " " + coordinates.size());

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    try {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
                        firstZoom = false;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "กำลังค้นหาตำแหน่ง", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
