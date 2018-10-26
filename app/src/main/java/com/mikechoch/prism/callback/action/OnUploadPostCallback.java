package com.mikechoch.prism.callback.action;

import com.mikechoch.prism.attribute.PrismPost;

public interface OnUploadPostCallback {

    void onSuccess(PrismPost prismPost);
    void onPermissionDenied();
    void onProgressUpdate(int progress);
    void onImageUploadFail(Exception e);
    void onPostUploadFail(Exception e);

}
