package com.mikechoch.prism.fire.callback;

public interface OnFetchEmailForUsernameCallback {

    void onSuccess(String email);
    void onFailure();
}
