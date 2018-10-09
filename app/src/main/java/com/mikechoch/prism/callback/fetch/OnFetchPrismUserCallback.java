package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismUser;

public interface OnFetchPrismUserCallback {

    void onSuccess(PrismUser prismUser);
    void onUserNotFound();
    void onFailure(Exception e);
}
