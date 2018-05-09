package com.mikechoch.prism.type;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.NotificationSettingsActivity;
import com.mikechoch.prism.constant.Default;

/**
 * Created by mikechoch on 2/16/18.
 */

public enum SettingType {

    APP("App Settings", R.drawable.ic_settings_white_36dp, Default.SETTINGS_OPTION_APP, null),
    NOTIFICATION("Notification Settings",  R.drawable.ic_bell_white_36dp, Default.SETTINGS_OPTION_NOTIFICATION, NotificationSettingsActivity.class),
    ACCOUNT("Account Settings",  R.drawable.ic_account_settings_variant_white_36dp, Default.SETTINGS_OPTION_ACCOUNT, null),
    HELP("Help",  R.drawable.ic_help_white_36dp, Default.SETTINGS_OPTION_HELP, null),
    ABOUT("About",  R.drawable.ic_information_outline_white_36dp, Default.SETTINGS_OPTION_ABOUT, null),
    LOGOUT("Logout",  R.drawable.ic_logout_white_36dp, Default.SETTINGS_OPTION_LOGOUT, null);

    private final String optionTitle;
    private final int optionIcon;
    private final int optionId;
    private final Class intentActivity;

    SettingType(String title, int icon, int id, Class intentActivity) {
        this.optionTitle = title;
        this.optionIcon = icon;
        this.optionId = id;
        this.intentActivity = intentActivity;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public int getOptionIcon() {
        return optionIcon;
    }

    public int getOptionId() {
        return optionId;
    }

    public Intent getIntent(Context context) {
        return new Intent(context, this.intentActivity);
    }

}
