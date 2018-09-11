package com.mikechoch.prism.fire.callback;

public interface OnChangePasswordCallback {
    void onSuccess();
    void onFailure(Exception exception);
    void onIncorrectPassword(Exception exception);
}
