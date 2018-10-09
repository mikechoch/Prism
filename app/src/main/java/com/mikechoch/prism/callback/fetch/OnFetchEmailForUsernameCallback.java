package com.mikechoch.prism.callback.fetch;

public interface OnFetchEmailForUsernameCallback {

    void onSuccess(String email);
    void onAccountNotFound();
    void onFailure(Exception exception);
}
