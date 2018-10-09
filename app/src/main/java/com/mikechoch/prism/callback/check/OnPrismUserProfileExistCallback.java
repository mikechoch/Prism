package com.mikechoch.prism.callback.check;

public interface OnPrismUserProfileExistCallback {

    void onSuccess(boolean prismUserExists);
    void onFailure(Exception exception);
}
