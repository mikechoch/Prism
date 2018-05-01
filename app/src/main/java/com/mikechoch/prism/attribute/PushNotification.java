package com.mikechoch.prism.attribute;

import com.mikechoch.prism.type.NotificationType;

public class PushNotification {

    private String mostRecentUsername;
    private String mostRecentUserProfilePicUri;
    private int otherUserCount;
    private NotificationType type;
    private long actionTimestamp;
    private int notificationHashId;
    private String prismPostId;
    private String prismUserId;

    public String getPrismPostId() {
        return prismPostId;
    }

    public void setPrismPostId(String prismPostId) {
        this.prismPostId = prismPostId;
    }

    public String getPrismUserId() {
        return prismUserId;
    }

    public void setPrismUserId(String prismUserId) {
        this.prismUserId = prismUserId;
    }

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

    public int getNotificationHashId() {
        return notificationHashId;
    }

    public void setNotificationHashId(int notificationHashId) {
        this.notificationHashId = notificationHashId;
    }
}
