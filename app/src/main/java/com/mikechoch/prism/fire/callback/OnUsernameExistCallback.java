package com.mikechoch.prism.fire.callback;

public interface OnUsernameExistCallback {

    void onSuccess(boolean usernameExists);
    void onFailure();
}
