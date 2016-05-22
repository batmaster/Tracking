package com.bananalab.tracking.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bananalab.tracking.model.Coordinate;
import com.bananalab.tracking.model.Tracking;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by batmaster on 5/23/16 AD.
 */
public class FireBaseHelper {

    public static void saveTracking(final Context context, final int t_id) {
        final Tracking tracking = DBHelper.getTracking(context, t_id);

        final ArrayList<Coordinate> coordinates = DBHelper.getCoordinates(context, t_id);

        final Firebase rootP = new Firebase("https://tracking-b.firebaseio.com/");

        final Firebase trackingsP = rootP.child(Preferences.getAccount(context).getEmail().replace('.', '_')).child("trackings");
        trackingsP.push().setValue(tracking, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                Firebase coordinatesP = rootP.child(Preferences.getAccount(context).getEmail().replace('.', '_')).child("coordinates");
                coordinatesP.push().setValue(coordinates, new Firebase.CompletionListener() {

                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            DBHelper.setHasSync(context, t_id, 1);

                            Toast.makeText(context, "Saved to online database.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("FireBase", firebaseError.getDetails());
                        }
                    }
                });
            }
        });


    }
}
