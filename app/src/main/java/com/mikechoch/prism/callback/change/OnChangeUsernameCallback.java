package com.mikechoch.prism.callback.change;

public interface OnChangeUsernameCallback {
    void onSuccess();
    void onUsernameTaken();
    void onFailure(Exception exception);
}
