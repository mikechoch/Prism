package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum MoreOption {

    REPORT("Report", R.drawable.ic_message_alert_outline_white_36dp),
    SHARE("Share", R.drawable.ic_share_white_36dp),
    DELETE("Delete", R.drawable.ic_delete_white_36dp);

    final String title;
    final int icon;

    MoreOption(String title, int icon) {
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
