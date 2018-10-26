package com.mikechoch.prism.attribute;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.mikechoch.prism.constant.Key;

import java.io.Serializable;

public class PrismPost implements Serializable, Comparable<PrismPost> {

    private String image;
    private String caption;
    private long timestamp;
    private String uid;


    // Attributes not saved in cloud
    private Integer likes;
    private Integer reposts;
    private String postId;
    private PrismUser prismUser;
    private Boolean isReposted;

    // Empty Constructor required by Firebase to convert DataSnapshot to PrismPost.class
    public PrismPost() { }

    public PrismPost(String image, String caption, String uid, long timestamp) {
        this.image = image;
        this.caption = caption;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    @PropertyName(Key.POST_IMAGE_URI)
    public String getImage() {
        return image;
    }

    @PropertyName(Key.POST_DESC)
    public String getCaption() {
        return caption;
    }

    @PropertyName(Key.POST_TIMESTAMP)
    public long getTimestamp() {
        return timestamp;
    }

    @PropertyName(Key.POST_UID)
    public String getUid() {
        return uid;
    }

    @Exclude
    public String getPostId() {
        return postId;
    }

    @Exclude
    public Integer getLikes() {
        return this.likes == null ? 0 : likes;
    }

    @Exclude
    public Integer getReposts() {
        return this.reposts == null ? 0 : reposts;
    }

    @Exclude
    public PrismUser getPrismUser() {
        return prismUser;
    }

    @Exclude
    public Boolean isReposted() {
        return isReposted;
    }

    @Exclude
    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Exclude
    public void setReposts(int reposts) {
        this.reposts = reposts;
    }

    @Exclude
    public void setPrismUser(PrismUser prismUser) {
        this.prismUser = prismUser;
    }

    @Exclude
    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Exclude
    public void setIsReposted(Boolean isReposted) {
        this.isReposted = isReposted;
    }

    @Override
    public int compareTo(@NonNull PrismPost o) {
        return Long.compare(this.getTimestamp(), o.getTimestamp());
    }
}