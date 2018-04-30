package com.mikechoch.prism.attribute;

import com.mikechoch.prism.type.NotificationType;

public class PushNotification {

    private String mostRecentUsername;
    private String mostRecentUserProfilePicUri;
    private int otherUserCount;
    private NotificationType type;
    private long actionTimestamp;
    private int notificationId;

    public String getMostRecentUsername() {
        return mostRecentUsername;
    }

    public void setMostRecentUsername(String mostRecentUsername) {
        this.mostRecentUsername = mostRecentUsername;
    }

    public String getMostRecentUserProfilePicUri() {
        return mostRecentUserProfilePicUri;
    }

    public void setMostRecentUserProfilePicUri(String mostRecentUserProfilePicUri) {
        this.mostRecentUserProfilePicUri = mostRecentUserProfilePicUri;
    }

    public int getOtherUserCount() {
        return otherUserCount;
    }

    public void setOtherUserCount(int otherUserCount) {
        this.otherUserCount = otherUserCount;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public long getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(long actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
