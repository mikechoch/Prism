package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.IntentHelper;


public class UpdateAppRequiredActivity extends AppCompatActivity {

    private TextView appNotToDateMessageTextView;
    private LinearLayout updateLinearLayout;
    private TextView updateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_app_required_activity_layout);

        appNotToDateMessageTextView = findViewById(R.id.updated_app_required_activity_text_view);
        updateLinearLayout = findViewById(R.id.updated_app_required_activity_refresh_button);
        updateTextView = findViewById(R.id.updated_app_required_activity_refresh_text_view);

        setupInterfaceElements();
    }

    /**
     *
     */
    private void setupUpdateButton() {
        updateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.resetApplication(UpdateAppRequiredActivity.this);
            }
        });
    }

    /**
     * Setup elements in current activity
     */
    private void setupInterfaceElements() {
        appNotToDateMessageTextView.setTypeface(Default.sourceSansProBold);
        updateTextView.setTypeface(Default.sourceSansProBold);

        setupUpdateButton();
    }

}
