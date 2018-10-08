package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum Setting {

    APP("App Settings", R.drawable.ic_settings_white_36dp, Default.SETTINGS_OPTION_APP),
    NOTIFICATION("Notification Settings",  R.drawable.ic_bell_white_36dp, Default.SETTINGS_OPTION_NOTIFICATION),
    ACCOUNT("Account Settings",  R.drawable.ic_account_settings_variant_white_36dp, Default.SETTINGS_OPTION_ACCOUNT),
    HELP("Help",  R.drawable.ic_help_white_36dp, Default.SETTINGS_OPTION_HELP),
    ABOUT("About",  R.drawable.ic_information_outline_white_36dp, Default.SETTINGS_OPTION_ABOUT),
    LOGOUT("Logout",  R.drawable.ic_logout_white_36dp, Default.SETTINGS_OPTION_LOGOUT);

    private final String title;
    private final int icon;
    private final int id;

    Setting(String title, int icon, int id) {
        this.title = title;
        this.icon = icon;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

}
