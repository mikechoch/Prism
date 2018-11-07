package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.OldNotification;

import java.util.ArrayList;

public interface OnFetchNotificationsCallback {

    void onSuccess(ArrayList<OldNotification> oldNotifications);
    void onNotificationsNotFound();
    void onFailure(Exception e);

}
