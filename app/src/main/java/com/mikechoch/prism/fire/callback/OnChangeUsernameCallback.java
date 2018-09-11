package com.mikechoch.prism.fire.callback;

public interface OnChangeUsernameCallback {
    void onSuccess();
    void onUsernameTaken();
    void onFailure(Exception exception);
}
