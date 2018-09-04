package com.mikechoch.prism.fire.callback;

public interface OnPrismUserProfileExistCallback {

    void onSuccess(boolean prismUserExists);
    void onFailure();
}
