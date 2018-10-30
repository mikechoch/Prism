package com.mikechoch.prism.type;

public enum DisplayUserType {

    LIKED_USERS("Like"),
    REPOSTED_USERS("Repost"),
    FOLLOWER_USERS("Follower"),
    FOLLOWING_USERS("Following");

    private String toolbarTitle;

    DisplayUserType(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }
}
