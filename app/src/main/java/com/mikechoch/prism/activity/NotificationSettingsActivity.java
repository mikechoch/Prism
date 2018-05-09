package com.mikechoch.prism.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.UserPreference;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.type.NotificationType;

public class NotificationSettingsActivity extends AppCompatActivity {

    Switch likeSwitch;
    Switch repostSwitch;
    Switch followSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings_activity_layout);

        likeSwitch = findViewById(R.id.notification_like_switch);
        repostSwitch = findViewById(R.id.notification_repost_switch);
        followSwitch = findViewById(R.id.notification_follow_switch);

        UserPreference userPreference = CurrentUser.preference;
        likeSwitch.setChecked(userPreference.allowLikePushNotification());
        repostSwitch.setChecked(userPreference.allowRepostPushNotification());
        followSwitch.setChecked(userPreference.allowFollowPushNotification());


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
}
