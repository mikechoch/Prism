package com.mikechoch.prism.callback.change;

public interface OnChangePasswordCallback {
    void onSuccess();
    void onFailure(Exception exception);
    void onIncorrectPassword(Exception exception);
}
