package com.mikechoch.prism.fire.callback;

import com.google.firebase.auth.FirebaseUser;

public interface OnFirebaseUserRegistrationCallback {

    void onSuccess(FirebaseUser firebaseUser);
    void onFailure(Object exception);

}
