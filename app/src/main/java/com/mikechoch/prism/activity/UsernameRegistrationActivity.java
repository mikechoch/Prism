package com.mikechoch.prism.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikechoch.prism.R;
import com.mikechoch.prism.callback.fetch.OnFetchUserProfileCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.FirebaseProfileAction;
import com.mikechoch.prism.callback.action.OnPrismUserRegistrationCallback;
import com.mikechoch.prism.callback.check.OnUsernameTakenCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.helper.ProfileHelper;

public class UsernameRegistrationActivity extends AppCompatActivity {

    private Boolean SHOULD_SIGN_OUT = Boolean.TRUE;

    private TextView usernamePromptTextView;
    private TextInputLayout usernameTextInputLayout;
    private EditText usernameEditText;
    private Button nextButton;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_registration_activity_layout);

        usernamePromptTextView = findViewById(R.id.register_username_activity_prompt_text_view);
        usernameTextInputLayout = findViewById(R.id.register_username_activity_text_input_layout);
        usernameEditText = findViewById(R.id.register_username_activity_edit_text);
        nextButton = findViewById(R.id.register_username_activity_next_button);

        usernamePromptTextView.setTypeface(Default.sourceSansProLight);
        usernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        usernameEditText.setTypeface(Default.sourceSansProLight);

        Intent intent = getIntent();
        fullName = intent.getStringExtra(Default.USERNAME_REGISTRATION_EXTRA);

        usernameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = ProfileHelper.getFormattedUsername(usernameEditText);
                createPrismUser(inputUsername);
            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(final CharSequence s, int i, int i1, int i2) {
                nextButton.setEnabled(true);
                usernameTextInputLayout.setErrorEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProfileHelper.isUsernameValid(s.toString().trim(), usernameTextInputLayout);
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        usernameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        String inputUsername = ProfileHelper.getFormattedUsername(usernameEditText);
                        createPrismUser(inputUsername);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPause() {
        if (SHOULD_SIGN_OUT) {
            Helper.toast(this, Message.GOOGLE_SIGN_IN_FAIL);
            FirebaseAuth.getInstance().signOut();
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        super.onPause();
    }

    /**
     *
     * @param username
     */
    public void createPrismUser(String username) {
        String firebaseEncodedUsername = ProfileHelper.getFirebaseEncodedUsername(username);
        if (ProfileHelper.isUsernameValid(username, usernameTextInputLayout)) {
            FirebaseProfileAction.isUsernameTaken(firebaseEncodedUsername, new OnUsernameTakenCallback() {
                @Override
                public void onSuccess(boolean usernameTaken) {
                    if (usernameTaken) {
                        usernameTextInputLayout.setError(Message.USERNAME_TAKEN);
                    } else {
                        FirebaseProfileAction.createPrismUserInFirebase(CurrentUser.getFirebaseUser(), fullName, firebaseEncodedUsername, new OnPrismUserRegistrationCallback() {
                            @Override
                            public void onSuccess() {
                                SHOULD_SIGN_OUT = Boolean.FALSE;
                                CurrentUser.prepareApp(UsernameRegistrationActivity.this);
                            }
                        });
                    }
                }

                @Override
                public void onFailure() {
                    Helper.toast(UsernameRegistrationActivity.this, Message.USERNAME_REGISTRATION_FAIL);
                }
            });
        }
    }

 }
