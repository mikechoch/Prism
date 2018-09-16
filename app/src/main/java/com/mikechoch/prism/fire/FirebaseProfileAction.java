package com.mikechoch.prism.fire;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.activity.LoginActivity;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.callback.OnChangeEmailCallback;
import com.mikechoch.prism.fire.callback.OnChangeFullNameCallback;
import com.mikechoch.prism.fire.callback.OnChangeUsernameCallback;
import com.mikechoch.prism.fire.callback.OnFetchEmailForUsernameCallback;
import com.mikechoch.prism.fire.callback.OnFirebaseUserRegistrationCallback;
import com.mikechoch.prism.fire.callback.OnChangePasswordCallback;
import com.mikechoch.prism.fire.callback.OnPrismUserProfileExistCallback;
import com.mikechoch.prism.fire.callback.OnPrismUserRegistrationCallback;
import com.mikechoch.prism.fire.callback.OnSendResetPasswordEmailCallback;
import com.mikechoch.prism.fire.callback.OnUsernameTakenCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.ProfileHelper;

import java.util.HashMap;
import java.util.Map;

public class FirebaseProfileAction {

    /**
     *
     * @param username
     * @param callback
     */
    public static void isUsernameTaken(String username, OnUsernameTakenCallback callback) {
        DatabaseReference accountsReference = Default.ACCOUNTS_REFERENCE;

        accountsReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usernameSnapshot) {
                boolean usernameTaken = usernameSnapshot.exists();
                callback.onSuccess(usernameTaken);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }
        });
    }

    /**
     *
     * @param user
     * @param fullname
     * @param firebaseEncodedUsername
     * @param callback
     */
    public static void createPrismUserInFirebase(FirebaseUser user, String fullname, String firebaseEncodedUsername, OnPrismUserRegistrationCallback callback) {
        DatabaseReference accountReference = Default.ACCOUNTS_REFERENCE;
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(user.getUid());
        DatabaseReference notificationPreference = currentUserReference.child(Key.DB_REF_USER_PREFERENCES);
        String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : ProfileHelper.generateDefaultProfilePic();

        Map<String, Object> profileMap = new HashMap<String, Object>() {{
            put(Key.USER_PROFILE_FULL_NAME, fullname);
            put(Key.USER_PROFILE_USERNAME, firebaseEncodedUsername);
            put(Key.USER_PROFILE_PIC, photoUrl);
        }};

        Map<String, Object> preferencesMap = new HashMap<String, Object>() {{
            put(Key.PREFERENCE_ALLOW_LIKE_NOTIFICATION, true);
            put(Key.PREFERENCE_ALLOW_REPOST_NOTIFICATION, true);
            put(Key.PREFERENCE_ALLOW_FOLLOW_NOTIFICATION, true);
        }};

        currentUserReference.updateChildren(profileMap);
        notificationPreference.updateChildren(preferencesMap);
        accountReference.child(firebaseEncodedUsername).setValue(user.getEmail());
        user.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(firebaseEncodedUsername).build());

        callback.onSuccess();
    }

    public static void doesUserHaveUsername(FirebaseUser firebaseUser, OnPrismUserProfileExistCallback callback) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot currentUserSnapshot) {
                callback.onSuccess(currentUserSnapshot.hasChild(Key.USER_PROFILE_USERNAME));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }
        });
    }

    public static void registerUserWithEmailAndPassword(String email, String password, OnFirebaseUserRegistrationCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    callback.onSuccess(user);
                } else {
                    Log.e(Default.TAG_DB, Message.USER_ACCOUNT_CREATION_FAIL);
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        callback.onFailure(weakPassword);
                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                        callback.onFailure(invalidEmail);
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        callback.onFailure(existEmail);
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                }
            }
        });
    }


    public static void fetchEmailForUsername(String username, OnFetchEmailForUsernameCallback callback) {
        DatabaseReference usernameAccountReference = Default.ACCOUNTS_REFERENCE.child(username);
        usernameAccountReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usernameSnapshot) {
                if (usernameSnapshot.exists()) {
                    callback.onSuccess((String) usernameSnapshot.getValue());
                } else {
                    callback.onAccountNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void changePassword(String oldPassword, String newPassword, OnChangePasswordCallback callback) {
        String email = CurrentUser.getFirebaseUser().getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        CurrentUser.getFirebaseUser().reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CurrentUser.getFirebaseUser().updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onFailure(e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onIncorrectPassword(e);
                    }
                });
    }

    public static void changeEmail(String password, String newEmail, OnChangeEmailCallback callback) {
        String currentEmail = CurrentUser.getFirebaseUser().getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

        CurrentUser.getFirebaseUser().reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CurrentUser.getFirebaseUser().updateEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference accountReference = Default.ACCOUNTS_REFERENCE;
                                            accountReference.child(CurrentUser.prismUser.getUsername())
                                                    .setValue(newEmail);
                                            callback.onSuccess();
                                        } else {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                                callback.onInvalidEmail(invalidEmail);
                                            } catch (FirebaseAuthUserCollisionException emailExists) {
                                                callback.onEmailAlreadyExist(emailExists);
                                            } catch (Exception e) {
                                                callback.onFailure(e);
                                            }
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(callback::onIncorrectPassword);
    }

    public static void changeUsername(String oldUsername, String newUsername, OnChangeUsernameCallback callback) {
        String oldFirebaseEncodedUsername = ProfileHelper.getFirebaseEncodedUsername(oldUsername);
        String newFirebaseEncodedUsername = ProfileHelper.getFirebaseEncodedUsername(newUsername);
        FirebaseProfileAction.isUsernameTaken(newFirebaseEncodedUsername, new OnUsernameTakenCallback() {
            @Override
            public void onSuccess(boolean usernameTaken) {
                if (usernameTaken) {
                    callback.onUsernameTaken();
                } else {
                    String oldUsernameAccountPath = Key.DB_REF_ACCOUNTS + "/" + oldFirebaseEncodedUsername;
                    String newUsernameAccountPath = Key.DB_REF_ACCOUNTS + "/" + newFirebaseEncodedUsername;
                    String newUsernameProfilePath = Key.DB_REF_USER_PROFILES + "/" + CurrentUser.prismUser.getUid() + "/" + Key.USER_PROFILE_USERNAME;

                    HashMap<String, Object> usernamePaths = new HashMap<String, Object>() {{
                        put(oldUsernameAccountPath, null);
                        put(newUsernameAccountPath, CurrentUser.getFirebaseUser().getEmail());
                        put(newUsernameProfilePath, newFirebaseEncodedUsername);
                    }};

                    DatabaseReference rootReference = Default.ROOT_REFERENCE;
                    rootReference.updateChildren(usernamePaths)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CurrentUser.getFirebaseUser().updateProfile(
                                            new UserProfileChangeRequest
                                                    .Builder()
                                                    .setDisplayName(newFirebaseEncodedUsername)
                                                    .build());

                                    CurrentUser.prismUser.setUsername(newUsername);

                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(callback::onFailure);
                }
            }

            @Override
            public void onFailure() { }
        });
    }

    public static void changeFullName(String newFullName, OnChangeFullNameCallback callback) {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        currentUserReference
                .child(Key.USER_PROFILE_FULL_NAME)
                .setValue(newFullName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CurrentUser.prismUser.setFullName(newFullName);
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void sendResetPasswordEmail(String email, OnSendResetPasswordEmailCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        if (signInMethodQueryResult.getSignInMethods().contains("password")) {
                            sendEmail();
                        } else {
                            callback.onAccountNotFoundForEmail();
                        }
                    }

                    void sendEmail(){
                        auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(callback::onFailure);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

}
