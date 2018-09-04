package com.mikechoch.prism.fire.callback;

public interface OnUsernameTakenCallback {

    void onSuccess(boolean usernameTaken);
    void onFailure();
}
