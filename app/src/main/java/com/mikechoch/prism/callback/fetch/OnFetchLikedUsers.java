package com.mikechoch.prism.callback.fetch;

import com.mikechoch.prism.attribute.PrismUser;

import java.util.ArrayList;

public interface OnFetchLikedUsers {

    void onSuccess(ArrayList<PrismUser> users);
    void onLikedUsersNotFound();
    void onFailure(Exception e);

}
