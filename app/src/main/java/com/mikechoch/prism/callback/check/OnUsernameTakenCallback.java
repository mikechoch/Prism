package com.mikechoch.prism.callback.check;

public interface OnUsernameTakenCallback {

    void onSuccess(boolean usernameTaken);
    void onFailure();
}
