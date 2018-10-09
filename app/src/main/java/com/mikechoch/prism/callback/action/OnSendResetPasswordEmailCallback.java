package com.mikechoch.prism.callback.action;

public interface OnSendResetPasswordEmailCallback {
    void onSuccess();
    void onAccountNotFoundForEmail();
    void onFailure(Exception exception);
}
