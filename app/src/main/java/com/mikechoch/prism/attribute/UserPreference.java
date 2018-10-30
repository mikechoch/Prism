package com.mikechoch.prism.attribute;

import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.type.NotificationType;

public class UserPreference {

    private boolean allowLikePushNotification;
    private boolean allowRepostPushNotification;
    private boolean allowFollowPushNotification;

    public UserPreference(boolean allowLikePushNotification, boolean allowRepostPushNotification, boolean allowFollowPushNotification) {
        this.allowLikePushNotification = allowLikePushNotification;
        this.allowRepostPushNotification = allowRepostPushNotification;
        this.allowFollowPushNotification = allowFollowPushNotification;
    }

    private void setAllowLikePushNotification(boolean allowLikePushNotification) {
        this.allowLikePushNotification = allowLikePushNotification;
    }

    private void setAllowRepostPushNotification(boolean allowRepostPushNotification) {
        this.allowRepostPushNotification = allowRepostPushNotification;
    }

    private void setAllowFollowPushNotification(boolean allowFollowPushNotification) {
        this.allowFollowPushNotification = allowFollowPushNotification;
    }

    public boolean doesAllowPushNotification(NotificationType type) {
        switch (type) {
            case LIKE:
                return this.allowLikePushNotification;

            case REPOST:
                return this.allowRepostPushNotification;
            case FOLLOW:
                return this.allowFollowPushNotification;
        }
        return true;
    }

    public void setPushNotificationPreference(NotificationType type, boolean allowPushNotification) {
        switch (type) {
            case LIKE:
                CurrentUser.preference.setAllowLikePushNotification(allowPushNotification);
                break;

            case REPOST:
                CurrentUser.preference.setAllowRepostPushNotification(allowPushNotification);
                break;

            case FOLLOW:
                CurrentUser.preference.setAllowFollowPushNotification(allowPushNotification);
                break;
        }
    }

}
