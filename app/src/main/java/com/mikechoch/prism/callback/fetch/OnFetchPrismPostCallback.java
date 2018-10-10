package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismPost;

public interface OnFetchPrismPostCallback {

    void onSuccess(PrismPost prismPost);
    void onPostNotFound();
    void onPostAuthorNotFound();
    void onFailure(Exception e);
}
