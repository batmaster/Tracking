package com.bananalab.tracking.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Tracking;
import com.bananalab.tracking.service.DBHelper;
import com.bananalab.tracking.service.LocationBackgroundService;
import com.bananalab.tracking.service.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTracking;
    private ArrayList<Tracking> trackings;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button button;

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1909;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewTracking = (RecyclerView) findViewById(R.id.recyclerViewTracking);

        layoutManager = new LinearLayoutManager(this);
        recyclerViewTracking.setLayoutManager(layoutManager);

//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                if (swipeDir == ItemTouchHelper.RIGHT) {
//                    Toast.makeText(getApplicationContext(), "right...", Toast.LENGTH_SHORT).show();
//                }
//                //Remove swiped item from list and notify the RecyclerView
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerViewTracking);



        button = (Button) findViewById(R.id.button);
        button.setText(Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP) == -1 ? "Record" : "Stop");
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP) == -1) {
                    DBHelper.startTracking(getApplicationContext());

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);

                        return;
                    }

                    startService(new Intent(getApplicationContext(), LocationBackgroundService.class));

                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.putExtra("t_id", Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP));
                    startActivity(intent);
                    onResume();
                }
                else {

                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    startActivity(intent);

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            boolean denied = false;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    denied = true;
                }
            }
            if (denied) {
                Toast.makeText(getApplicationContext(), "Cannot start tracking because location permission is not granted.", Toast.LENGTH_SHORT).show();
            }
            else {
                startService(new Intent(getApplicationContext(), LocationBackgroundService.class));

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("t_id", Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP));
                startActivity(intent);
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        button.setText(Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP) == -1 ? "Record" : "Stop");

        trackings = DBHelper.getTrackings(getApplicationContext());
        adapter = new ListsAdapter(MainActivity.this, trackings);
        recyclerViewTracking.setAdapter(adapter);
    }

}
