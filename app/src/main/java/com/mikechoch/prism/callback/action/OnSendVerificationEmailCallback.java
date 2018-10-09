package com.mikechoch.prism.callback.action;

public interface OnSendVerificationEmailCallback {

    void onSuccess();
    void onFailure(Exception e);

}
