package com.mikechoch.prism.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LinkedNotifications {

    private LinkedHashMap<String, Notification> notificationsHashMap;
    private HashSet<String> mostRecentUserIds;

    public LinkedNotifications() {
        notificationsHashMap = new LinkedHashMap<>();
        mostRecentUserIds = new HashSet<>();
    }

    public void addNotification(Notification notification) {
        notificationsHashMap.put(notification.getNotificationId(), notification);
        mostRecentUserIds.add(notification.getMostRecentUid());
    }

    public Collection<Notification> getNotifications() {
        return notificationsHashMap.values();
    }

    public LinkedHashMap<String, Notification> getNotificationsHashMap() {
        return notificationsHashMap;
    }

    public Collection<String> getMostRecentUserIds() {
        return mostRecentUserIds;
    }

    public Collection<String> getPostBasedNotificationPostIds() {
        ArrayList<String> postIds = new ArrayList<>();
        for (Notification notification : notificationsHashMap.values()) {
            if (notification instanceof PostBasedNotification) {
                PostBasedNotification postBasedNotification = (PostBasedNotification) notification;
                postIds.add(postBasedNotification.getPostId());
            }
        }
        return postIds;
    }

    private void removeNotification(Notification notification) {
        notificationsHashMap.remove(notification.getNotificationId());
    }

    public void updateMostRecentPrismUsersForNotifications(LinkedPrismUsers linkedPrismUsers) {
        for (Notification notification : notificationsHashMap.values()) {
            PrismUser prismUser = linkedPrismUsers.getPrismUser(notification.getMostRecentUid());
            if (prismUser != null) {
                notification.setMostRecentPrismUser(prismUser);
            } else {
                removeNotification(notification);
            }
        }
    }

    public void updatePrismPostForPostBasedNotifications(LinkedPrismPosts linkedPrismPosts) {
        for (Notification notification : notificationsHashMap.values()) {
            if (notification instanceof PostBasedNotification) {
                PostBasedNotification postBasedNotification = (PostBasedNotification) notification;
                PrismPost prismPost = linkedPrismPosts.getPrismPost(((PostBasedNotification) notification).getPostId());
                if (prismPost != null) {
                    postBasedNotification.setPrismPost(prismPost);
                } else {
                    removeNotification(notification);
                }
            }
        }
    }
}
