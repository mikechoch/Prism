package com.mikechoch.prism.fire.callback;

import java.util.ArrayList;

public interface OnFetchCallback {

    void onSuccess(ArrayList<Object> fetchResults);
    void onFailure();

}
