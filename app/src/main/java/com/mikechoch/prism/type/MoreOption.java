package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum MoreOption {

    REPORT("Report", R.drawable.ic_message_alert_outline_white_36dp, Default.MORE_OPTION_REPORT),
    SHARE("Share", R.drawable.ic_share_white_36dp, Default.MORE_OPTION_SHARE),
    DELETE("Delete", R.drawable.ic_delete_white_36dp, Default.MORE_OPTION_DELETE);

    final String title;
    final int icon;
    final int id;

    MoreOption(String title, int icon, int id) {
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
