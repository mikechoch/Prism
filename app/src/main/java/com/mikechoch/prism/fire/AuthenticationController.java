package com.mikechoch.prism.fire;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.RegisterUsernameActivity;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.fire.callback.OnPrismUserProfileExistCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.ProfileHelper;
import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;

import java.util.List;

public class AuthenticationController {


    private static FirebaseAuth auth = FirebaseAuth.getInstance();


    /**
     * Creates a GoogleSignInClient using GoogleSignInOptions. This is the first thing
     * required for the GoogleSignIn process, without the client, the app won't be
     * able to access user's Google Accounts on the phone. The googleSignInClient
     * object cannot be static because it has a Context attribute
     * @param context - Used to getString
     * @return GoogleSignInClient object
     */
    public static GoogleSignInClient buildGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .requestId()
                .build();
        return GoogleSignIn.getClient(context, gso);
    }

    /**
     * Gets invoked when user clicks the "Sign in with Google" button. It uses the
     * googleSignInClient to create a pop up that asks user to choose a Google account
     * on their device. If the user chooses an account, the result of that will be
     * received in LoginActivity.onActivityResult() and gets handled in handleGoogleIntentResult()
     * @param context - Used to invoke googleSignIn intent (the pop up with Google accounts)
     * @param googleSignInClient - Used to get signIn intent
     */
    public static void initiateSignInWithGoogle(Context context, GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        ((Activity) context).startActivityForResult(signInIntent, Default.SIGN_IN_WITH_GOOGLE_REQUEST_CODE);
    }

    /**
     * Gets called after the user's data is received from googleSignInClient. An attempt
     * to create AuthCredentials is made using the GoogleSignInAccount object and if the
     * authCredentials object is passed into another method to start the authentication process
     * @param context - Gets passed along to authentication method
     * @param data - Holds user's google account and other data that gets extracted
     */
    public static void handleGoogleIntentResult(final Context context, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            authenticateCredentials(((Activity) context), account, credential);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.e(Default.TAG_GOOGLE_CLIENT, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            Helper.toast(context, "Failed to Sign in with Google");
        }
    }

    /**
     * Checks to see if an account already exists for the given email. It happens by
     * fetching a list of signIn methods associated with the email. If the email isn't already
     * registered then authCredential object is used and FirebaseAuth sign in happens. If that is
     * successful, then the user is signed in to the app and eventually gets taken to Home page
     * @param context - Gets passed along methods
     * @param account - Used to get email and check if email is already registered or not
     * @param credential - Used to perform Firebase Authentication
     */
    private static void authenticateCredentials(final Activity context, final GoogleSignInAccount account, final AuthCredential credential) {
        auth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (!isEmailAlreadyRegisteredWithPassword(context, account.getEmail(), task.getResult().getSignInMethods())) {
                        performSignInWithCredential(context, credential);
                    }
                }
            }

            /**
             * Gets invoked if email is not registered. If Firebase Auth is successful,
             * authResult.getUser (FirebaseUser) is taken to main page
             */
            void performSignInWithCredential(final Activity context, AuthCredential credential) {
                auth.signInWithCredential(credential)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser firebaseUser = authResult.getUser();
                                // TODO check if user needs to create a username or not
                                FirebaseProfileAction.doesUserHaveUsername(firebaseUser, new OnPrismUserProfileExistCallback() {
                                    @Override
                                    public void onSuccess(boolean prismUserExists) {
                                        if (prismUserExists) {
                                            signInUser(context);
                                        } else {
                                            Intent intent = new Intent(context, RegisterUsernameActivity.class);
                                            intent.putExtra("fullName", account.getDisplayName());
                                            context.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Helper.toast(context, "Failed to Sign in");
                            }
                        });
            }
        });
    }

    private static CustomAlertDialogBuilder createUsernameDialog(Context context, FirebaseUser firebaseUser) {

        View chooseUsernameView = ((Activity)context).getLayoutInflater().inflate(R.layout.choose_username_alert_dialog_layout, null);
        RelativeLayout chooseUsernameRelativeLayout = chooseUsernameView.findViewById(R.id.choose_username_alert_dialog_relative_layout);

        TextView chooseUsernameHeaderTextView = chooseUsernameView.findViewById(R.id.choose_username_header_text_view);
        TextInputLayout chooseUsernameTextInputLayout = chooseUsernameView.findViewById(R.id.choose_username_text_input_layout);
        EditText chooseUsernameEditText = chooseUsernameView.findViewById(R.id.choose_username_edit_text);
        ProgressBar chooseUsernameProgressBar = chooseUsernameView.findViewById(R.id.choose_username_progress_bar);

        chooseUsernameHeaderTextView.setTypeface(Default.sourceSansProLight);
        chooseUsernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        chooseUsernameEditText.setTypeface(Default.sourceSansProLight);

        CustomAlertDialogBuilder chooseUsernameAlertDialog = new CustomAlertDialogBuilder(context, chooseUsernameRelativeLayout);
        chooseUsernameAlertDialog.setView(chooseUsernameView);
        chooseUsernameAlertDialog.setIsCancelable(true);
        chooseUsernameAlertDialog.setCanceledOnTouchOutside(false);
        chooseUsernameAlertDialog.setPositiveButton(Default.BUTTON_SUBMIT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseUsernameProgressBar.setVisibility(View.VISIBLE);

                // Register firebaseUser -- TODO This is almost same as the one in register activity, so Generify it
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                String username = ProfileHelper.getFirebaseEncodedUsername(email.split("@")[0]);
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                firebaseUser.updateProfile(profile);

                DatabaseReference usersReference = Default.USERS_REFERENCE;
                DatabaseReference profileReference = usersReference.child(uid);
                profileReference.child(Key.USER_PROFILE_FULL_NAME).setValue(firebaseUser.getDisplayName());
                profileReference.child(Key.USER_PROFILE_USERNAME).setValue(username);

                String profilePic;
                if (firebaseUser.getPhotoUrl() != null) {
                    profilePic = firebaseUser.getPhotoUrl().toString();
                } else {
                    profilePic = ProfileHelper.generateDefaultProfilePic();
                }
                profileReference.child(Key.USER_PROFILE_PIC).setValue(profilePic);

                DatabaseReference notificationPreference = profileReference.child(Key.DB_REF_USER_PREFERENCES);
                notificationPreference.child(Key.PREFERENCE_ALLOW_LIKE_NOTIFICATION).setValue(true);
                notificationPreference.child(Key.PREFERENCE_ALLOW_REPOST_NOTIFICATION).setValue(true);
                notificationPreference.child(Key.PREFERENCE_ALLOW_FOLLOW_NOTIFICATION).setValue(true);

                DatabaseReference accountReference = Default.ACCOUNT_REFERENCE.child(username);
                accountReference.setValue(email);


            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        chooseUsernameProgressBar.setVisibility(View.GONE);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) { }
        });
        return chooseUsernameAlertDialog;
    }




    /**
     * Takes user's name and email, and saves them in Firebase Database. And then takes the
     * user to main page of the app. finish() is called to clear backStack so user can't go
     * back to Login page onBackPress
     * @param context - Used to intent user and clear back stack
     */
    private static void signInUser(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        CurrentUser.prepareAppForUser(context, intent);
    }

    /**
     * Checks to see if the given email is registered by checking to see if signInMethods for
     * the given email contains a password-method or not. If so, then a Snackbar is displayed
     * to the user with the given email. Authentication won't proceed if email is alreadyRegistered
     * @param context - Used to make Snackbar
     * @param signInMethodsForEmail - List of methods associated with the email for sign in
     * @return True if email is list contains a password method for email, False otherwise
     */
    private static boolean isEmailAlreadyRegisteredWithPassword(Context context, String email, List<String> signInMethodsForEmail) {
        boolean alreadyRegistered = signInMethodsForEmail != null && signInMethodsForEmail.contains("password");
        if (alreadyRegistered) {
            RelativeLayout view = ((Activity) context).findViewById(R.id.login_relative_layout);
            Snackbar.make(view, "Email " + email + " is already registered. Please login with password", Snackbar.LENGTH_LONG).show();

        }
        return alreadyRegistered;
    }


}
