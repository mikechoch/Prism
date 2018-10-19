package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismPost;

import java.util.ArrayList;

public interface OnFetchPrismPostsCallback {

    void onSuccess(ArrayList<PrismPost> prismPosts);
    void onPrismPostsNotFound();
    void onFailure(Exception e);

}
