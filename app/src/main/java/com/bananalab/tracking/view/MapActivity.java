package com.bananalab.tracking.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback, NotifiableMapActivity {

    private int t_id;
    private GoogleMap googleMap;

    private Button buttonStop;

    public LatLng prevLoc;
    public boolean firstZoom = true;

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

//        Location myLocation = googleMap.getMy/Location();
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));


        if (t_id == Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP)) {
            DBHelper.setNotifiableMapActivity(MapActivity.this);
        }

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                ArrayList<Coordinate> coordinates = DBHelper.getCoordinates(getApplicationContext(), t_id);
                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < coordinates.size(); i++) {
                    LatLng curLoc = new LatLng(coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude());

                    if (prevLoc != null) {
                        googleMap.addPolyline(new PolylineOptions()
                                .add(prevLoc, curLoc)
                                .width(6)
                                .color(Color.GREEN)
                                .visible(true)
                        );
                    }
                    prevLoc = curLoc;
                    builder.include(curLoc);
                }

                Log.d("DBH map", "onMapReady " + t_id + " " + coordinates.size());

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
    // TODO check again
    @Override
    public void notifyMap(Coordinate newCoordinate) {
        Log.d("DBH map", "notifyMap " + newCoordinate);

        LatLng curLoc = new LatLng(newCoordinate.getLatitude(), newCoordinate.getLongitude());

        if (prevLoc != null) {
            googleMap.addPolyline(new PolylineOptions()
                    .add(prevLoc, curLoc)
                    .width(6)
                    .color(Color.GREEN)
                    .visible(true)
            );
        }

        prevLoc = curLoc;
        if (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(curLoc)) {
            if (firstZoom) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));
            }
            else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLoc, googleMap.getCameraPosition().zoom));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBHelper.removeNotifiableMapActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonStop.setVisibility(Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP) == -1 ? View.GONE : View.VISIBLE);
    }
}
