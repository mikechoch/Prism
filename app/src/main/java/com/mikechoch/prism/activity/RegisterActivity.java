package com.mikechoch.prism.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.mikechoch.prism.R;
import com.mikechoch.prism.callback.action.OnSendVerificationEmailCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fire.FirebaseProfileAction;
import com.mikechoch.prism.callback.action.OnFirebaseUserRegistrationCallback;
import com.mikechoch.prism.callback.action.OnPrismUserRegistrationCallback;
import com.mikechoch.prism.callback.check.OnUsernameTakenCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.helper.ProfileHelper;


public class RegisterActivity extends AppCompatActivity {

    private ImageView iconImageView;
    private TextInputLayout fullNameTextInputLayout;
    private EditText fullNameEditText;
    private TextInputLayout usernameTextInputLayout;
    private EditText usernameEditText;
    private TextInputLayout emailTextInputLayout;
    private EditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView goToLoginButton;
    private ProgressBar registerProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);
        fullNameTextInputLayout = findViewById(R.id.register_name_text_input_layout);
        fullNameEditText = findViewById(R.id.register_name_edit_text);
        usernameTextInputLayout = findViewById(R.id.register_username_text_input_layout);
        usernameEditText = findViewById(R.id.register_username_edit_text);
        emailTextInputLayout = findViewById(R.id.register_email_text_input_layout);
        emailEditText = findViewById(R.id.register_email_edit_text);
        passwordTextInputLayout = findViewById(R.id.register_password_text_input_layout);
        passwordEditText = findViewById(R.id.register_password_edit_text);
        registerButton = findViewById(R.id.register_submit_button);
        goToLoginButton = findViewById(R.id.login_text_view);
        registerProgressBar = findViewById(R.id.register_progress_bar);

        setupUIElements();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Setup the image view at the top of the Register Default.screen
     * 30% of the Default.screen will be the width and margin the image top by 1/16th of the height
     */
    private void setupIconImageView() {
        iconImageView.getLayoutParams().width = (int) (Default.screenWidth * 0.3);
    }

    /**
     * FullName EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupFullNameEditText() {
        final Handler handler = new Handler();
        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameTextInputLayout.setErrorEnabled(false);
                handler.removeCallbacksAndMessages(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            ProfileHelper.isFullNameValid(s.toString().trim(), fullNameTextInputLayout);
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Username EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupUsernameEditText() {
        final Handler handler = new Handler();
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameTextInputLayout.setErrorEnabled(false);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            ProfileHelper.isUsernameValid(s.toString().trim(), usernameTextInputLayout);
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable e) {
            }
        });
    }

    /**
     * Email EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupEmailEditText() {
        final Handler handler = new Handler();
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailTextInputLayout.setErrorEnabled(false);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            ProfileHelper.isEmailValid(s.toString().trim(), emailTextInputLayout);
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Password EditTextLayout Typefaces are set and TextWatcher is setup for error handling
     */
    private void setupPasswordEditText() {
        passwordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        passwordTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);
        final Handler handler = new Handler();
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordTextInputLayout.setErrorEnabled(false);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() > 0) {
                            ProfileHelper.isPasswordValid(s.toString().trim(), passwordTextInputLayout);
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * Setup the register button
     * When all fields are valid the button is enabled
     * When clicked, an attempt to create the account with the entered credentials
     */
    private void setupRegisterButton() {
        registerButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleProgressBar(true);
                final String fullName = ProfileHelper.getFormattedFullName(fullNameEditText);
                final String inputUsername = ProfileHelper.getFormattedUsername(usernameEditText);
                final String firebaseEncodedUsername = ProfileHelper.getFirebaseEncodedUsername(inputUsername);
                final String email = ProfileHelper.getFormattedEmail(emailEditText);
                final String password = ProfileHelper.getFormattedPassword(passwordEditText);


                if (!ProfileHelper.areRegistrationCredentialsValid(fullName, email, inputUsername, password,
                        fullNameTextInputLayout, emailTextInputLayout, usernameTextInputLayout, passwordTextInputLayout)) {
                    toggleProgressBar(false);
                    return;
                }


                FirebaseProfileAction.isUsernameTaken(firebaseEncodedUsername, new OnUsernameTakenCallback() {
                    @Override
                    public void onSuccess(boolean usernameTaken) {
                        if (usernameTaken) {
                            usernameTextInputLayout.setError("Username is taken. Try again");
                            toggleProgressBar(false);
                            return;
                        }

                        FirebaseProfileAction.registerUserWithEmailAndPassword(email, password, new OnFirebaseUserRegistrationCallback() {
                            @Override
                            public void onSuccess(FirebaseUser firebaseUser) {
                                FirebaseProfileAction.createPrismUserInFirebase(firebaseUser, fullName, firebaseEncodedUsername, new OnPrismUserRegistrationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        DatabaseAction.sendVerificationEmail(firebaseUser, new OnSendVerificationEmailCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Helper.toast(RegisterActivity.this, "An email has been sent to " + email + ". Please click on the verfication link");
                                                IntentHelper.intentToEmailVerificationActivity(RegisterActivity.this, true);
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Helper.toast(RegisterActivity.this, "Unable to send verification email");
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Object exception) {
                                toggleProgressBar(false);
                                if (exception instanceof FirebaseAuthWeakPasswordException) {
                                    passwordTextInputLayout.setError("Password is too weak");
                                }
                                if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    emailTextInputLayout.setError("Invalid email");
                                }
                                if (exception instanceof FirebaseAuthUserCollisionException) {
                                    emailTextInputLayout.setError("Email already exists");
                                }
                                if (exception instanceof Exception) {
                                    ((Exception) exception).printStackTrace();
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure() {
                        toggleProgressBar(false);
                    }
                });

            }
        });
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        // Setup Typefaces for all text based UI elements
        fullNameTextInputLayout.setTypeface(Default.sourceSansProLight);
        fullNameEditText.setTypeface(Default.sourceSansProLight);
        usernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        usernameEditText.setTypeface(Default.sourceSansProLight);
        emailTextInputLayout.setTypeface(Default.sourceSansProLight);
        emailEditText.setTypeface(Default.sourceSansProLight);
        passwordTextInputLayout.setTypeface(Default.sourceSansProLight);
        passwordEditText.setTypeface(Default.sourceSansProLight);
        registerButton.setTypeface(Default.sourceSansProLight);
        goToLoginButton.setTypeface(Default.sourceSansProLight);

        setupIconImageView();
        setupFullNameEditText();
        setupUsernameEditText();
        setupEmailEditText();
        setupPasswordEditText();
        setupRegisterButton();
        setupLoginButton();

    }

    /**
     * Setup the button to take you back to Login Activity
     */
    private void setupLoginButton() {
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.super.onBackPressed();
            }
        });
    }

    /**
     * Toggles the register button and login button to hide and shows the progress bar spinner
     */
    private void toggleProgressBar(boolean showProgressBar) {
        int progressVisibility = showProgressBar ? View.VISIBLE : View.INVISIBLE;
        int buttonVisibility = showProgressBar ? View.INVISIBLE : View.VISIBLE;
        registerButton.setVisibility(buttonVisibility);
        goToLoginButton.setVisibility(buttonVisibility);
        registerProgressBar.setVisibility(progressVisibility);
    }

}
