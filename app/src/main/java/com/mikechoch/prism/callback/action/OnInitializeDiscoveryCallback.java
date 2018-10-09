package com.mikechoch.prism.callback.action;

public interface OnInitializeDiscoveryCallback {

    void onSuccess();
    void onFailure(Exception exception);

}
