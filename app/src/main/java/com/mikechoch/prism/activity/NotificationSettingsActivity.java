package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.type.NotificationType;


public class NotificationSettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTextView;

    private Switch likeSwitch;
    private Switch repostSwitch;
    private Switch followSwitch;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings_activity_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);

        likeSwitch = findViewById(R.id.notification_like_switch);
        repostSwitch = findViewById(R.id.notification_repost_switch);
        followSwitch = findViewById(R.id.notification_follow_switch);

        setupInterfaceElements();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Setup all user notification switches
     * Set checked to the current correct user notification preference
     * Setup on check changed listener to update Firebase with correct notification switch values
     */
    private void initializeSwitches() {
        likeSwitch.setChecked(CurrentUser.preference.doesAllowPushNotification(NotificationType.LIKE));
        repostSwitch.setChecked(CurrentUser.preference.doesAllowPushNotification(NotificationType.REPOST));
        followSwitch.setChecked(CurrentUser.preference.doesAllowPushNotification(NotificationType.FOLLOW));

        likeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseAction.updatePreferenceForPushNotification(NotificationType.LIKE, isChecked);
            }
        });

        repostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseAction.updatePreferenceForPushNotification(NotificationType.REPOST, isChecked);
            }
        });

        followSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseAction.updatePreferenceForPushNotification(NotificationType.FOLLOW, isChecked);
            }
        });
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Setup elements in current activity
     */
    private void setupInterfaceElements() {
        toolbarTextView.setTypeface(Default.sourceSansProBold);
        likeSwitch.setTypeface(Default.sourceSansProBold);
        repostSwitch.setTypeface(Default.sourceSansProBold);
        followSwitch.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        initializeSwitches();
    }
}
