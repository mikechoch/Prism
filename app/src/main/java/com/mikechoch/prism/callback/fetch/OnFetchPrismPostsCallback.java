package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismPost;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFetchPrismPostsCallback {

    void onSuccess(HashMap<String, PrismPost> prismPostsMap);
    void onPrismPostsNotFound();
    void onFailure(Exception e);

}
