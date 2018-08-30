package com.mikechoch.prism;

import java.util.ArrayList;

public interface OnFetchListener {

    void onPostsSuccess(ArrayList<Object> fetchResults);
    void onFailure();

}
