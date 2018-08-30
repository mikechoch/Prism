package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum Discovery {

    USER("Users", R.drawable.ic_account_white_36dp),
    LIKE("Most Liked", R.drawable.like_heart),
    REPOST("Most Reposted", R.drawable.repost_iris),
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
