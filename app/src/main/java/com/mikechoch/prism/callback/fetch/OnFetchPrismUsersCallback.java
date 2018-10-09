package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismUser;

import java.util.ArrayList;

public interface OnFetchPrismUsersCallback {

    void onSuccess(ArrayList<PrismUser> prismUsers);
    void onFailure(Exception e);

}
