package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.LinkedNotifications;

public interface OnFetchNotificationsCallback {

    void onSuccess(LinkedNotifications linkedNotifications);
    void onNotificationsNotFound();
    void onFailure(Exception e);

}
