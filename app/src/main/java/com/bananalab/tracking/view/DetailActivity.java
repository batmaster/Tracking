package com.bananalab.tracking.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Tracking;
import com.bananalab.tracking.service.DBHelper;
import com.bananalab.tracking.service.LocationBackgroundService;
import com.bananalab.tracking.service.Preferences;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Tracking tracking = DBHelper.getTracking(getApplicationContext(), Preferences.getInt(getApplicationContext(), Preferences.TRACKING_ID_TEMP));

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "บันทึกเรียบร้อย", Toast.LENGTH_SHORT).show();

                String title = editTextTitle.getText().toString();
                if (title.equals("")) {
                    tracking.setTitle("ไม่ได้ตั้งชื่อ");
                }
                else {
                    tracking.setTitle(title);
                }

                String description = editTextDescription.getText().toString();
                tracking.setDescription(description);

                DBHelper.finishTracking(getApplicationContext(), tracking);

                stopService(new Intent(getApplicationContext(), LocationBackgroundService.class));
                finish();
            }
        });

    }
}
