package com.mikechoch.prism.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LinkedPrismPosts {

    private LinkedHashMap<String, PrismPost> prismPostsHashMap;
    private HashSet<String> prismUserIds;

    public LinkedPrismPosts() {
        prismPostsHashMap = new LinkedHashMap<>();
        prismUserIds = new HashSet<>();
    }

    public void addPrismPost(PrismPost prismPost) {
        prismPostsHashMap.put(prismPost.getPostId(), prismPost);
        prismUserIds.add(prismPost.getUid());
    }

    public Collection<PrismPost> getPrismPosts() {
        return prismPostsHashMap.values();
    }

    public Collection<String> getPrismUserIds() {
        //return new ArrayList<>(prismUserIds);
        return prismUserIds;
    }

    public PrismPost getPrismPost(String prismPostId) {
        return prismPostsHashMap.get(prismPostId);
    }

    public LinkedHashMap<String, PrismPost> getPrismPostsHashMap() {
        return prismPostsHashMap;
    }

    private void removePrismPost(PrismPost prismPost) {
        prismPostsHashMap.remove(prismPost.getPostId());
    }

    public void updatePrismUsersForPosts(LinkedPrismUsers linkedPrismUsers) {
        for (PrismPost prismPost : prismPostsHashMap.values()) {
            PrismUser prismUser = linkedPrismUsers.getPrismUser(prismPost.getUid());
            if (prismUser != null) {
                prismPost.setPrismUser(prismUser);
            } else {
                removePrismPost(prismPost);
            }
        }
    }

}
