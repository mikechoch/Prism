package com.mikechoch.prism.callback.change;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public interface OnChangeEmailCallback {
    void onSuccess();
    void onFailure(Exception exception);
    void onIncorrectPassword(Exception exception );
    void onEmailAlreadyExist(FirebaseAuthUserCollisionException existEmail);
    void onInvalidEmail(FirebaseAuthInvalidCredentialsException invalidEmail);

}
