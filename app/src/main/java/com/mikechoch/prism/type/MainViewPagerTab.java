package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum MainViewPagerTab {

    MAIN_FEED(Default.MAIN_VIEW_PAGER_MAIN_FEED, R.drawable.ic_image_filter_hdr_white_36dp),
    SEARCH(Default.MAIN_VIEW_PAGER_SEARCH, R.drawable.ic_magnify_white_36dp),
    NOTIFICATION(Default.MAIN_VIEW_PAGER_NOTIFICATIONS, R.drawable.ic_bell_white_36dp),
    PROFILE(Default.MAIN_VIEW_PAGER_PROFILE, R.drawable.ic_account_white_36dp);

    final private int id;
    final private int icon;

    MainViewPagerTab(int id, int icon) {
        this.id = id;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }
}
