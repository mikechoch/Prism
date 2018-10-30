package com.mikechoch.prism.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.AuthenticationController;
import com.mikechoch.prism.fire.FirebaseProfileAction;
import com.mikechoch.prism.callback.fetch.OnFetchEmailForUsernameCallback;
import com.mikechoch.prism.callback.action.OnSendResetPasswordEmailCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.helper.ProfileHelper;
import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;

public class LoginActivity extends AppCompatActivity {

    private ImageView iconImageView;

    private TextInputLayout emailOrUsernameTextInputLayout;
    private EditText emailOrUsernameEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;
    private TextInputLayout resetEmailTextInputLayout;
    private EditText resetEmailEditText;

    private Button loginButton;
    private Button forgotPassword;
    private Button goToRegisterButton;
    private SignInButton googleSignInButton;

    private ProgressBar loginProgressBar;
    private ProgressBar forgotPasswordProgressBar;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);
        emailOrUsernameTextInputLayout = findViewById(R.id.email_text_input_layout);
        emailOrUsernameEditText = findViewById(R.id.email_edit_text);
        passwordTextInputLayout = findViewById(R.id.password_text_input_layout);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_submit_button);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        forgotPassword = findViewById(R.id.forgot_password_button);
        goToRegisterButton = findViewById(R.id.go_to_register_button);
        loginProgressBar = findViewById(R.id.login_progress_bar);

        setupUIElements();
        googleSignInClient = AuthenticationController.buildGoogleSignInClient(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Default.SIGN_IN_WITH_GOOGLE_REQUEST_CODE) {
            AuthenticationController.handleGoogleIntentResult(LoginActivity.this, data);
            toggleLoginProgressBar(false);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
     * Password EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupPasswordEditText() {
        passwordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        passwordTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);
    }

    /**
     *
     */
    private void setupForgotPassword() {
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createForgotPasswordAlertDialog().show();
            }
        });
    }



    private void sendResetPasswordEmail(String email, DialogInterface dialog) {
        FirebaseProfileAction.sendResetPasswordEmail(email, new OnSendResetPasswordEmailCallback() {
            @Override
            public void onSuccess() {
                Helper.toast(LoginActivity.this, Message.RESET_PASSWORD_EMAIL_SENT, true);
                dialog.dismiss();
            }

            @Override
            public void onAccountNotFoundForEmail() {
                Helper.toast(LoginActivity.this, Message.ACCOUNT_NOT_FOUND, true);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                Helper.toast(LoginActivity.this, Message.RESET_PASSWORD_EMAIL_FAIL, true);
                dialog.dismiss();
            }
        });

    }

    private CustomAlertDialogBuilder createForgotPasswordAlertDialog() {
        View resetPasswordView = getLayoutInflater().inflate(R.layout.reset_password_alert_dialog_layout, null);
        RelativeLayout resetPasswordRelativeLayout = resetPasswordView.findViewById(R.id.reset_password_alert_dialog_relative_layout);

        TextView forgotPasswordHeaderTextView = resetPasswordView.findViewById(R.id.forgot_password_header_text_view);
        resetEmailTextInputLayout = resetPasswordView.findViewById(R.id.reset_password_input_email_text_input_layout);
        resetEmailEditText = resetPasswordView.findViewById(R.id.reset_password_input_email_edit_text);
        forgotPasswordProgressBar = resetPasswordView.findViewById(R.id.reset_password_progress_bar);

        forgotPasswordHeaderTextView.setTypeface(Default.sourceSansProLight);
        resetEmailTextInputLayout.setTypeface(Default.sourceSansProLight);
        resetEmailEditText.setTypeface(Default.sourceSansProLight);

        CustomAlertDialogBuilder resetPasswordAlertDialog = new CustomAlertDialogBuilder(this, resetPasswordRelativeLayout);
        resetPasswordAlertDialog.setView(resetPasswordView);
        resetPasswordAlertDialog.setIsCancelable(true);
        resetPasswordAlertDialog.setCanceledOnTouchOutside(false);
        resetPasswordAlertDialog.setPositiveButton(Default.BUTTON_SUBMIT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = ProfileHelper.getFormattedEmail(resetEmailEditText);
                if (ProfileHelper.isEmailValid(email, resetEmailTextInputLayout)) {
                    forgotPasswordProgressBar.setVisibility(View.VISIBLE);
                    sendResetPasswordEmail(email, dialog);
                }
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null);

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

                String emailOrUsername = ProfileHelper.getFormattedEmail(emailOrUsernameEditText);
                String password = ProfileHelper.getFormattedPassword(passwordEditText);
                boolean isEmail = ProfileHelper.isInputOfTypeEmail(emailOrUsername);

                if (!ProfileHelper.areLoginCredentialsValid(emailOrUsername, password, passwordTextInputLayout, emailOrUsernameTextInputLayout)) {
                    toggleLoginProgressBar(false);
                    return;
                }

                if (isEmail) {
                    String email = emailOrUsername;
                    attemptLogin(email, password);
                } else {
                    String username = ProfileHelper.getFirebaseEncodedUsername(emailOrUsername);
                    FirebaseProfileAction.fetchEmailForUsername(username, new OnFetchEmailForUsernameCallback() {
                        @Override
                        public void onSuccess(String email) {
                            attemptLogin(email, password);
                        }

                        @Override
                        public void onAccountNotFound() {
                            emailOrUsernameTextInputLayout.setError(Message.ACCOUNT_NOT_FOUND);
                            toggleLoginProgressBar(false);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            toggleLoginProgressBar(false);
                            emailOrUsernameTextInputLayout.setError(Message.FAILED_TO_LOGIN);
                        }
                    });
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
        emailOrUsernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        emailOrUsernameEditText.setTypeface(Default.sourceSansProLight);
        passwordTextInputLayout.setTypeface(Default.sourceSansProLight);
        passwordEditText.setTypeface(Default.sourceSansProLight);
        loginButton.setTypeface(Default.sourceSansProLight);
        forgotPassword.setTypeface(Default.sourceSansProLight);
        goToRegisterButton.setTypeface(Default.sourceSansProLight);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        TextView googleSignInButtonTextView = ((TextView) googleSignInButton.getChildAt(0));
        googleSignInButtonTextView.setTypeface(Default.sourceSansProLight);

        setupIconImageView();
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
                toggleLoginProgressBar(true);
                AuthenticationController.initiateSignInWithGoogle(LoginActivity.this, googleSignInClient);
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
     * Perform validation checks before attempting sign in
     * Also display a loading spinner until onComplete
     */
    private void attemptLogin(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getUser().isEmailVerified()) {
                                IntentHelper.intentToMainActivity(LoginActivity.this, true);
                            } else {
                                IntentHelper.intentToEmailVerificationActivity(LoginActivity.this, true);
                            }

                        } else {
                            toggleLoginProgressBar(false);
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException noEmailFound) {
                                emailOrUsernameTextInputLayout.setError(Message.ACCOUNT_NOT_FOUND);
                            } catch (Exception e) {
                                passwordTextInputLayout.setError(Message.INVALID_PASSWORD);
                                toggleLoginProgressBar(false);
                            }
                        }
                    }
                });
    }

    /**
     * Toggles the login button and register button to hide and shows the progress bar spinner
     */
    private void toggleLoginProgressBar(boolean showProgressBar) {
        int progressVisibility = showProgressBar ? View.VISIBLE : View.GONE;
        int buttonVisibility = showProgressBar ? View.GONE : View.VISIBLE;
        loginButton.setVisibility(buttonVisibility);
        goToRegisterButton.setVisibility(buttonVisibility);
        googleSignInButton.setVisibility(buttonVisibility);
        forgotPassword.setVisibility(buttonVisibility);

        loginProgressBar.setVisibility(progressVisibility);
    }

}
