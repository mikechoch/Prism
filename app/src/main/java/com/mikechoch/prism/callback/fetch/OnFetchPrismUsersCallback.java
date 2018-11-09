package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismUser;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFetchPrismUsersCallback {

    void onSuccess(HashMap<String, PrismUser> prismUsersMap);
    void onPrismUsersNotFound();
    void onFailure(Exception e);

}
