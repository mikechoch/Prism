package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;

/**
 * Created by parth on 3/4/18.
 */

public enum NotificationType {

    LIKE("like", Key.DB_REF_POST_LIKED_USERS, R.drawable.ic_notification_badge_heart_white_36dp, Default.DISPLAY_USERS_LIKE_CODE),
    UNLIKE("like", Key.DB_REF_POST_LIKED_USERS, R.drawable.ic_notification_badge_heart_white_36dp, Default.DISPLAY_USERS_LIKE_CODE),

    REPOST("repost", Key.DB_REF_POST_REPOSTED_USERS, R.drawable.ic_camera_iris_black_36dp, Default.DISPLAY_USERS_REPOST_CODE),
    UNREPOST("repost", Key.DB_REF_POST_REPOSTED_USERS, R.drawable.ic_camera_iris_black_36dp, Default.DISPLAY_USERS_REPOST_CODE),

    FOLLOW("follow", Key.DB_REF_USER_FOLLOWERS, R.drawable.ic_plus_circle_black_36dp, Default.DISPLAY_USERS_FOLLOWER_CODE),
    UNFOLLOW("follow", Key.DB_REF_USER_FOLLOWERS, R.drawable.ic_plus_circle_black_36dp, Default.DISPLAY_USERS_FOLLOWER_CODE);


    private final String notifIdSuffix;
    private final String DB_REF_KEY;
    private final int notifIcon;
    private final int notifUserDisplayCode;

    NotificationType(String notifIdSuffix, String dbRefKey, int notifIcon, int notifUserDisplayCode) {
        this.notifIdSuffix = notifIdSuffix;
        this.DB_REF_KEY = dbRefKey;
        this.notifIcon = notifIcon;
        this.notifUserDisplayCode = notifUserDisplayCode;
    }

    public String getNotifIdSuffix() {
        return "_" + notifIdSuffix;
    }

    public String getDatabaseRefKey() {
        return DB_REF_KEY;
    }

    public int getNotifIcon() {
        return notifIcon;
    }

    public int getNotifUserDisplayCode() {
        return notifUserDisplayCode;
    }

    public static NotificationType getNotificationType(String notificationId) {
        if (notificationId.endsWith("_like")) {
            return NotificationType.LIKE;
        }
        if (notificationId.endsWith("_repost")) {
            return NotificationType.REPOST;
        }
        if (notificationId.endsWith("_follow")) {
            return NotificationType.FOLLOW;
        }
        return null;
    }

    public static String getNotificationPostId(NotificationType type, String notificationId) {
        return notificationId.replace(type.getNotifIdSuffix(), "");
    }

    /**
     * Overriding the toString method for the NotificationType enum
     * Checks which enum type is being used
     * Used for displaying in each NotificationType item TexView
     * @return - returns the appropriate String related to the NotificationType
     */
    @Override
    public String toString() {
        switch (this) {
            case LIKE:
                return "liked";
            case REPOST:
                return "reposted";
            case FOLLOW:
                return "followed you";
            default:
                return "";
        }
    }
}
