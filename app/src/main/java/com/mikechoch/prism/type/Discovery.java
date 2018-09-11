package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum Discovery {

    LIKE("Most Liked", R.drawable.ic_heart_white_36dp),
    USER("Users", R.drawable.ic_account_white_36dp),
    REPOST("Most Reposted", R.drawable.ic_camera_iris_black_36dp),
    TAG("Tags", R.drawable.ic_pound_white_48dp);

    private final String title;
    private final int icon;

    Discovery(String title, int icon) {
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
