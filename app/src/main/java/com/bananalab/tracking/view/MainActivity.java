package com.bananalab.tracking.view;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTracking;
    private ArrayList<Tracking> trackings;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewTracking = (RecyclerView) findViewById(R.id.recyclerViewTracking);

        Tracking[] s = {
                new Tracking("บ้าน สายใต้", "1 ชั่วโมง 48 นาที", "02:30 24/04/2016", "42 กิโลเมตร"),
                new Tracking("เกษตร", "28 นาที", "12:30 24/04/2016", "8 กิโลเมตร"),
                new Tracking("รังสิต", "53 นาที", "16:30 24/04/2016", "11.6 กิโลเมตร"),
        };

        layoutManager = new LinearLayoutManager(this);
        recyclerViewTracking.setLayoutManager(layoutManager);

        trackings = new ArrayList<Tracking>(Arrays.asList(s));
        adapter = new ListsAdapter(trackings);
        recyclerViewTracking.setAdapter(adapter);

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
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Tracking t = new Tracking(new Date().toString(), "", "", "");
                trackings.add(t);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
