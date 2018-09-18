package com.mikechoch.prism.fire.callback;

public interface OnSendResetPasswordEmailCallback {
    void onSuccess();
    void onAccountNotFoundForEmail();
    void onFailure(Exception exception);
}
