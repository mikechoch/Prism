package com.mikechoch.prism.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;

public class UnderMaintenanceActivity extends AppCompatActivity {

    private TextView alertMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_maintenance_activity_layout);

        alertMessageTextView = findViewById(R.id.alert_text_view);
        alertMessageTextView.setTypeface(Default.sourceSansProBold);

        String alertMessage = getIntent().getExtras().getString(Key.STATUS_MESSAGE);
        alertMessageTextView.setText(alertMessage);

    }

}
