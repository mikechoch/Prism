package com.mikechoch.prism.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;

public class LoginActivity extends AppCompatActivity {

    /*
     * Globals
     */
    private FirebaseAuth auth;
    
    private ImageView iconImageView;

    private TextInputLayout emailTextInputLayout;
    private EditText emailOrUsernameEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;
    private TextInputLayout resetEmailTextInputLayout;
    private EditText resetEmailEditText;
    private TextView forgotPasswordHeaderTextView;

    private Button loginButton;
    private Button forgotPasswordTextView;
    private Button goToRegisterButton;
    private SignInButton googleSignInButton;

    private ProgressBar loginProgressBar;
    private ProgressBar forgotPasswordProgressBar;

    private CustomAlertDialogBuilder resetPasswordAlertDialog;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        // User authentication instance
        auth = FirebaseAuth.getInstance();

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);
        emailTextInputLayout = findViewById(R.id.email_text_input_layout);
        emailOrUsernameEditText = findViewById(R.id.email_edit_text);
        passwordTextInputLayout = findViewById(R.id.password_text_input_layout);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.register_submit_button);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password_button);
        goToRegisterButton = findViewById(R.id.go_to_register_button);
        loginProgressBar = findViewById(R.id.login_progress_bar);

        setupUIElements();
        buildGoogleSignInClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Default.SIGN_IN_WITH_GOOGLE_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            RelativeLayout view = findViewById(R.id.login_relative_layout);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();
                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getSignInMethods().isEmpty()) {
                                firebaseAuthWithGoogle(account);
                            } else {
                                Snackbar.make(view, "An account already exists with the email " + account.getEmail() + ". Please try to login using password or choose Forgot Password", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(Default.TAG_GOOGLE_CLIENT, "Unable to fetchSignInMethodsForEmail " + account.getEmail());
                            Helper.toast(LoginActivity.this, "Failed to Sign in with Google");
                        }
                    }
                });
                //firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.e(Default.TAG_GOOGLE_CLIENT, "signInResult:failed code=" + e.getStatusCode());
                e.printStackTrace();
                Helper.toast(LoginActivity.this, "Failed to Sign in with Google");
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            DatabaseReference usersReference = Default.USERS_REFERENCE.child(user.getUid());
                            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // TODO LOGIN
                                    } else {
                                        // TODO create user in firebase
                                        registerUser(user);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            // TODO check if user exists in Database or not. If user does not exist in database, register the user else sign in
                            // Before registering, check if user's email already exists

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(Default.TAG_GOOGLE_CLIENT, "signInWithCredential:failure", task.getException());
                            Helper.toast(LoginActivity.this, "Failed to Sign in with Google", true);
                            task.getException().printStackTrace();
                        }

                        // ...
                    }
                });
    }

    private void registerUser(FirebaseUser firebaseUser) {
        


    }

    private void buildGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void attemptLoginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Default.SIGN_IN_WITH_GOOGLE_REQUEST_CODE);
    }


    /**
     * Setup the image view at the top of the Login Default.screen
     * 50% of the Default.screen will be the width and margin the image top by 1/16th of the height
     */
    private void setupIconImageView() {
        iconImageView.getLayoutParams().width = (int) (Default.screenWidth * 0.5);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iconImageView.getLayoutParams();
        lp.setMargins(0, (Default.screenHeight/16), 0, 0);
        iconImageView.setLayoutParams(lp);
    }

    /**
     * Email EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupEmailEditText() {
        emailOrUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                emailTextInputLayout.setErrorEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            isEmailOrUsernameValid(s.toString().trim());
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    /**
     * Password EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupPasswordEditText() {
        passwordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        passwordTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                passwordTextInputLayout.setErrorEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            isPasswordValid(s.toString().trim());
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    /**
     *
     */
    private void setupForgotPassword() {
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createForgotPasswordAlertDialog().show();
            }
        });
    }

    private void sendResetPasswordEmail(String email, DialogInterface dialog) {
        forgotPasswordProgressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Helper.toast(LoginActivity.this, "Email sent", true);
                } else {
                     Helper.toast(LoginActivity.this, "Unable to send email. Please try again later", true);
                     Log.e(Default.TAG_DB, Message.PASSWORD_RESET_EMAIL_SEND_FAIL, task.getException());
                }
                forgotPasswordProgressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }

    private CustomAlertDialogBuilder createForgotPasswordAlertDialog() {
        View resetPasswordView = getLayoutInflater().inflate(R.layout.reset_password_alert_dialog_layout, null);
        RelativeLayout resetPasswordRelativeLayout = resetPasswordView.findViewById(R.id.reset_password_alert_dialog_relative_layout);

        forgotPasswordHeaderTextView = resetPasswordView.findViewById(R.id.forgot_password_header_text_view);
        resetEmailTextInputLayout = resetPasswordView.findViewById(R.id.reset_password_input_email_text_input_layout);
        resetEmailEditText = resetPasswordView.findViewById(R.id.reset_password_input_email_edit_text);
        forgotPasswordProgressBar = resetPasswordView.findViewById(R.id.reset_password_progress_bar);

        forgotPasswordHeaderTextView.setTypeface(Default.sourceSansProLight);
        resetEmailTextInputLayout.setTypeface(Default.sourceSansProLight);
        resetEmailEditText.setTypeface(Default.sourceSansProLight);

        resetPasswordAlertDialog = new CustomAlertDialogBuilder(this, resetPasswordRelativeLayout);
        resetPasswordAlertDialog.setView(resetPasswordView);
        resetPasswordAlertDialog.setIsCancelable(true);
        resetPasswordAlertDialog.setCanceledOnTouchOutside(false);
        resetPasswordAlertDialog.setPositiveButton(Default.BUTTON_SUBMIT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = resetEmailEditText.getText().toString();
                sendResetPasswordEmail(email, dialog);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        return resetPasswordAlertDialog;
    }

    /**
     * Login button is disabled, formatting is set, and OnClickListener is setup
     * When the login button is clicked, this should check whether it is a username or email
     * Error handle for invalid credentials and otherwise go to MainActivity
     */
    private void setupLoginButton() {
        loginButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLoginProgressBar(true);
                String emailOrUsername = getFormattedEmailOrUsername();
                String password = getFormattedPassword();

                if (!isEmailOrUsernameValid(emailOrUsername) || !isPasswordValid(password)) {
                    toggleLoginProgressBar(false);
                    return;
                }

                // If input is username, extract email from database
                if (!isInputOfTypeEmail(emailOrUsername)) {
                    DatabaseReference accountsReference = Default.ACCOUNT_REFERENCE.child(emailOrUsername);
                    accountsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String email = (String) dataSnapshot.getValue();
                                attemptLogin(email, password);
                            } else {
                                emailTextInputLayout.setError("Username not found");
                                toggleLoginProgressBar(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.wtf(Default.TAG_DB, Message.USER_EXIST_CHECK_FAIL, databaseError.toException());
                            Helper.toast(LoginActivity.this, "An error occurred logging in");
                            toggleLoginProgressBar(false);
                        }
                    });
                } else {
                    attemptLogin(emailOrUsername, password);
                }
            }
        });
    }

    /**
     * Setup the register button TextView to go to RegisterActivity when clicked
     */
    private void setupGoToRegisterButton() {
        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToRegisterActivity();
            }
        });
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        // Setup Typefaces for all text based UI elements
        emailTextInputLayout.setTypeface(Default.sourceSansProLight);
        emailOrUsernameEditText.setTypeface(Default.sourceSansProLight);
        passwordTextInputLayout.setTypeface(Default.sourceSansProLight);
        passwordEditText.setTypeface(Default.sourceSansProLight);
        loginButton.setTypeface(Default.sourceSansProLight);
        forgotPasswordTextView.setTypeface(Default.sourceSansProLight);
        goToRegisterButton.setTypeface(Default.sourceSansProLight);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        TextView googleSignInButtonTextView = ((TextView) googleSignInButton.getChildAt(0));
        googleSignInButtonTextView.setTypeface(Default.sourceSansProLight);

        setupIconImageView();
        setupEmailEditText();
        setupPasswordEditText();
        setupLoginButton();
        setupForgotPassword();
        setupGoToRegisterButton();
        setupSignInWithGoogleButton();
    }

    private void setupSignInWithGoogleButton() {
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut(); // TODO delete this
                attemptLoginWithGoogle();
            }
        });
    }

    /**
     * Intent to Register Activity from Login Activity
     */
    private void intentToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        Pair<View, String> iconPair = Pair.create(iconImageView, "icon");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(LoginActivity.this, iconPair);
        startActivity(registerIntent, options.toBundle());
        overridePendingTransition(0, 0);
    }

    /**
     * Intent to Main Activity from Login Activity
     * TODO Rename this method
     */
    private void intentToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        CurrentUser.prepareAppForUser(this, intent);
    }

    /**
     * Perform validation checks before attempting sign in
     * Also display a loading spinner until onComplete
     */
    private void attemptLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    intentToMainActivity();
                } else {
                    passwordTextInputLayout.setError("Invalid email/username or password");
                    toggleLoginProgressBar(false);
                    Log.i(Default.TAG_DB, Message.LOGIN_ATTEMPT_FAIL, task.getException());
                }
            }
        });
    }

    /**
     * Toggles the login button and register button to hide and shows the progress bar spinner
     */
    private void toggleLoginProgressBar(boolean showProgressBar) {
        int progressVisibility = showProgressBar ? View.VISIBLE : View.INVISIBLE;
        int buttonVisibility = showProgressBar ? View.INVISIBLE : View.VISIBLE;
        loginButton.setVisibility(buttonVisibility);
        goToRegisterButton.setVisibility(buttonVisibility);
        loginProgressBar.setVisibility(progressVisibility);
    }

    /**
     * Email/Username validation check
     */
    private boolean isEmailOrUsernameValid(String email) {
        boolean isValid = (Patterns.EMAIL_ADDRESS.matcher(email).matches()) ||
                (email.length() >=  5 && email.length() <= 30);
        if (!isValid) {
           emailTextInputLayout.setError("Invalid username/email");
        }
        return isValid;
    }

    /**
     * Password validation check
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Checks to see if what firebaseUser typed in the username/email editText
     * is of type email or username. The purpose is that if the firebaseUser
     * enters an email, we can directly attemptLogin otherwise for username,
     * we have to go to the database and extract the email for the given
     * username
     * @param emailOrUsername text from the email/username editText
     * @return True if input is an email and False if it's a username
     */
    private boolean isInputOfTypeEmail(String emailOrUsername) {
        return Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches();
    }

    /**
     * Cleans the email or username entered and returns the clean version
     */
    private String getFormattedEmailOrUsername() {
        return emailOrUsernameEditText.getText().toString().trim();
    }

    /**
     * Cleans the password entered and returns the clean version
     */
    private String getFormattedPassword() {
        return passwordEditText.getText().toString().trim();
    }

}
