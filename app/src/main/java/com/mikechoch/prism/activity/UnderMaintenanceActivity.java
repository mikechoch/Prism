package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

        setupInterfaceElements();
    }

    /**
     * Get the alert message String from Bundle of Intent from SplashScreenActivity
     * @return alert - alert message String
     */
    private String getAlertMessageString() {
        String alert = "Prism is currently under maintenance, please come back after 6pm EST. We apologize for the inconvenience.";
        Bundle incomingBundle = getIntent().getExtras();
        if (incomingBundle != null) {
            alert = getIntent().getExtras().getString(Key.STATUS_MESSAGE);
        }
        return alert;
    }

    /**
     * Setup the alert message text view text
     */
    private void setupAlertMessageView() {
        String alertMessage = getAlertMessageString();
        alertMessageTextView.setText(alertMessage);
    }

    /**
     * Setup elements in current activity
     */
    private void setupInterfaceElements() {
        alertMessageTextView.setTypeface(Default.sourceSansProBold);

        setupAlertMessageView();
    }

}
