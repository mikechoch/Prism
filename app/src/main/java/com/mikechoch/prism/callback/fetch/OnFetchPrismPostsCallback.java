package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.LinkedPrismPosts;
import com.mikechoch.prism.attribute.PrismPost;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFetchPrismPostsCallback {

    void onSuccess(LinkedPrismPosts linkedPrismPosts);
    void onPrismPostsNotFound();
    void onFailure(Exception e);

}
