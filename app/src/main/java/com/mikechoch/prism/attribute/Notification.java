package com.mikechoch.prism.attribute;

import android.support.annotation.NonNull;

import com.mikechoch.prism.type.NotificationType;

public abstract class Notification implements Comparable<Notification> {

    private String notificationId;
    private PrismUser mostRecentPrismUser;
    private String mostRecentUid;
    protected NotificationType notificationType;
    private long actionTimestamp;
    private boolean viewed;

    abstract String notificationMessage();

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public PrismUser getMostRecentPrismUser() {
        return mostRecentPrismUser;
    }

    public void setMostRecentPrismUser(PrismUser mostRecentPrismUser) {
        this.mostRecentPrismUser = mostRecentPrismUser;
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

    public String getMostRecentUid() {
        return mostRecentUid;
    }

    public void setMostRecentUid(String mostRecentUid) {
        this.mostRecentUid = mostRecentUid;
    }

    @Override
    public int compareTo(@NonNull Notification o) {
        return Long.compare(o.getActionTimestamp(), this.getActionTimestamp());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Notification) {
            Notification notification = (Notification) obj;
            return this.notificationId.equals(notification.notificationId);
        }
        return false;
    }
}
