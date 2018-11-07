package com.mikechoch.prism.attribute;

import android.support.annotation.NonNull;

import com.mikechoch.prism.type.NotificationType;

public class OldNotification implements Comparable<OldNotification> {

    private String notificationId;
    private NotificationType type;
    private PrismPost prismPost;
    private long actionTimestamp;
    private PrismUser mostRecentUser;
    private boolean viewed;

    public OldNotification() {

    }

    public OldNotification(String notificationId, NotificationType type, PrismPost prismPost, PrismUser mostRecentUser, long actionTimestamp, boolean viewed) {
        this.notificationId = notificationId;
        this.type = type;
        this.prismPost = prismPost;
        this.mostRecentUser = mostRecentUser;
        this.actionTimestamp = actionTimestamp;
        this.viewed = viewed;

    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public PrismPost getPrismPost() {
        return prismPost;
    }

    public void setPrismPost(PrismPost prismPost) {
        this.prismPost = prismPost;
    }

    public long getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(long actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public PrismUser getMostRecentUser() {
        return mostRecentUser;
    }

    public void setMostRecentUser(PrismUser mostRecentUser) {
        this.mostRecentUser = mostRecentUser;
    }

    public String getNotificationPostId() {
        if (this.type == NotificationType.LIKE || this.type == NotificationType.REPOST) {
            return notificationId.replace(this.type.getNotifIdSuffix(), "");
        }
        return "";
    }

    public String getNotificationUserId() {
        if (this.type == NotificationType.FOLLOW) {
            return notificationId.replace(this.type.getNotifIdSuffix(), "");
        }
        return "";
    }


    public Integer getOtherUserCount() {
        switch (type) {
            case LIKE:
                return prismPost.getLikes() > 1 ? prismPost.getLikes() - 1 : 0;
            case REPOST:
                return  prismPost.getReposts() > 1 ? prismPost.getReposts() -1 : 0;
            default:
                return 0;
        }
    }

    @Override
    public int compareTo(@NonNull OldNotification o) {
        return Long.compare(o.getActionTimestamp(), this.getActionTimestamp());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OldNotification) {
            OldNotification oldNotification = (OldNotification) obj;
            return this.notificationId.equals(oldNotification.notificationId);
        }
        return false;
    }
}
