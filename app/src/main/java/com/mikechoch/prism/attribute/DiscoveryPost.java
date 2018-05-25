package com.mikechoch.prism.attribute;

public class DiscoveryPost {

    public PrismUser followingUser;
    public PrismPost prismPost;
    /* This is the prismUser that CurrentUser is following and
    this prismUser has either liked or reposted the above prismPost */

    public DiscoveryPost(PrismPost prismPost, PrismUser followingUser) {
        this.prismPost = prismPost;
        this.followingUser = followingUser;
    }

}
