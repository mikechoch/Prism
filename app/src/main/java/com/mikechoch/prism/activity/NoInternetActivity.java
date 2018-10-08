package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.IntentHelper;


public class NoInternetActivity extends AppCompatActivity {

    private TextView noInternetTextView;
    private LinearLayout refreshButton;
    private TextView refreshTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_activity_layout);

        noInternetTextView = findViewById(R.id.no_internet_activity_text_view);
        refreshButton = findViewById(R.id.no_internet_activity_refresh_button);
        refreshTextView = findViewById(R.id.no_internet_activity_refresh_text_view);

        setupInterfaceElements();
    }

    /**
     *
     */
    private void setupRefreshButton() {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.resetApplication(NoInternetActivity.this);
            }
        });
    }

    /**
     * Setup elements in current activity
     */
    private void setupInterfaceElements() {
        noInternetTextView.setTypeface(Default.sourceSansProBold);
        refreshTextView.setTypeface(Default.sourceSansProBold);

        setupRefreshButton();
    }

}
