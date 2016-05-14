package com.bananalab.tracking.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback, NotifiableMapActivity {

    private int t_id;
    private GoogleMap googleMap;
    private ArrayList<Coordinate> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        t_id = getIntent().getIntExtra("t_id", -1);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public LatLng prevLoc;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Cannot open map because location permission is not granted.", Toast.LENGTH_SHORT).show();

            finish();
        }
        googleMap.setMyLocationEnabled(true);
//        Location myLocation = googleMap.getMy/Location();
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));

        coordinates = DBHelper.getCoordinates(getApplicationContext(), t_id);

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
        }

        Log.d("DBH map", "onMapReady " + t_id + " " + Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP));
        if (t_id == Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP)) {
            DBHelper.setNotifiableMapActivity(MapActivity.this);
        }
    }
    // TODO check again
    @Override
    public void notifyMap(Coordinate newCoordinate) {
        coordinates.add(newCoordinate);
        Log.d("DBH map", "notifyMap " + newCoordinate + " " + coordinates.size());

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBHelper.removeNotifiableMapActivity();
    }
}
