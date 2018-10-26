package com.mikechoch.prism.callback.action;

public interface OnDeletePostCallback {

    void onSuccess();
    void onPostNotFound();
    void onPermissionDenied();
    void onImageDeleteFail(Exception e);
    void onPostDeleteFail(Exception e);
}
