package com.mikechoch.prism.attribute;

public class UserPreference {

    private boolean allowLikePushNotification;
    private boolean allowRepostPushNotification;
    private boolean allowFollowPushNotification;

    public UserPreference(boolean allowLikePushNotification, boolean allowRepostPushNotification, boolean allowFollowPushNotification) {
        this.allowLikePushNotification = allowLikePushNotification;
        this.allowRepostPushNotification = allowRepostPushNotification;
        this.allowFollowPushNotification = allowFollowPushNotification;
    }

    public boolean allowLikePushNotification() {
        return allowLikePushNotification;
    }

    public void setAllowLikePushNotification(boolean allowLikePushNotification) {
        this.allowLikePushNotification = allowLikePushNotification;
    }

    public boolean allowRepostPushNotification() {
        return allowRepostPushNotification;
    }

    public void setAllowRepostPushNotification(boolean allowRepostPushNotification) {
        this.allowRepostPushNotification = allowRepostPushNotification;
    }

    public boolean allowFollowPushNotification() {
        return allowFollowPushNotification;
    }

    public void setAllowFollowPushNotification(boolean allowFollowPushNotification) {
        this.allowFollowPushNotification = allowFollowPushNotification;
    }
}
