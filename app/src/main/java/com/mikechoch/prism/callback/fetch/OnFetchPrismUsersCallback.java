package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.LinkedPrismUsers;
import com.mikechoch.prism.attribute.PrismUser;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFetchPrismUsersCallback {

    void onSuccess(LinkedPrismUsers linkedPrismUsers);
    void onPrismUsersNotFound();
    void onFailure(Exception e);

}
