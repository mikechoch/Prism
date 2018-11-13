package com.mikechoch.prism.attribute;

import com.mikechoch.prism.type.NotificationType;

public class PostBasedNotification extends Notification {

    private PrismPost prismPost;

    public PrismPost getPrismPost() {
        return prismPost;
    }

    public void setPrismPost(PrismPost prismPost) {
        this.prismPost = prismPost;
    }

    public Integer getOtherUserCount() {
        switch (this.notificationType) {
            case LIKE:
                return prismPost.getLikes() > 1 ? prismPost.getLikes() - 1 : 0;
            case REPOST:
                return  prismPost.getReposts() > 1 ? prismPost.getReposts() -1 : 0;
            default:
                return 0;
        }
    }

    public String getPostId() {
        return this.getNotificationId().replace(this.getNotificationType().getNotifIdSuffix(), "");
    }

    @Override
    String notificationMessage() {
        return null;
    }
}
