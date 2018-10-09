package com.mikechoch.prism.callback.action;

import com.google.firebase.auth.FirebaseUser;

public interface OnFirebaseUserRegistrationCallback {

    void onSuccess(FirebaseUser firebaseUser);
    void onFailure(Object exception);

}
