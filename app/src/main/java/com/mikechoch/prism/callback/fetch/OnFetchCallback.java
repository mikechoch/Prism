package com.mikechoch.prism.callback.fetch;

import java.util.ArrayList;

public interface OnFetchCallback {

    void onSuccess(ArrayList<Object> fetchResults);
    void onFailure();

}
