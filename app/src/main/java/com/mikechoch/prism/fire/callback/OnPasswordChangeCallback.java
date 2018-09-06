package com.mikechoch.prism.fire.callback;

public interface OnPasswordChangeCallback {
    void onSuccess();
    void onFailure(Exception exception);
    void onIncorrectPassword();
}
