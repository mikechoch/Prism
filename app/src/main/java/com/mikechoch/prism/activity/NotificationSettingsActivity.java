package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.UserPreference;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings_activity_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);

        setupToolbar();
        initializeSwitches();

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

    private void initializeSwitches() {
        likeSwitch = findViewById(R.id.notification_like_switch);
        repostSwitch = findViewById(R.id.notification_repost_switch);
        followSwitch = findViewById(R.id.notification_follow_switch);

        likeSwitch.setTypeface(Default.sourceSansProBold);
        repostSwitch.setTypeface(Default.sourceSansProBold);
        followSwitch.setTypeface(Default.sourceSansProBold);

        UserPreference userPreference = CurrentUser.preference;
        likeSwitch.setChecked(userPreference.allowLikePushNotification());
        repostSwitch.setChecked(userPreference.allowRepostPushNotification());
        followSwitch.setChecked(userPreference.allowFollowPushNotification());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTextView.setTypeface(Default.sourceSansProBold);
    }
}
