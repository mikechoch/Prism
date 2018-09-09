package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum MoreOption {

    REPORT("Report", R.drawable.ic_account_edit_white_36dp),
    SHARE("Share", R.drawable.ic_account_edit_white_36dp),
    DELETE("Delete", R.drawable.ic_account_edit_white_36dp);

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
