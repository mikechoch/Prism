package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum Setting {

    APP("App Settings", R.drawable.ic_settings_white_36dp),
    NOTIFICATION("Notification Settings",  R.drawable.ic_bell_white_36dp),
    ACCOUNT("Account Settings",  R.drawable.ic_account_edit_white_36dp),
    HELP("Help",  R.drawable.ic_help_white_36dp),
    ABOUT("About",  R.drawable.ic_information_outline_white_36dp),
    LOGOUT("Logout",  R.drawable.ic_logout_white_36dp);

    private final String title;
    private final int icon;

    Setting(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

}
