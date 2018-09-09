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
import com.google.firebase.auth.FirebaseUser;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.FirebaseProfileAction;
import com.mikechoch.prism.fire.callback.OnPrismUserRegistrationCallback;
import com.mikechoch.prism.fire.callback.OnUsernameTakenCallback;
import com.mikechoch.prism.helper.Helper;
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
        setContentView(R.layout.register_username_activity_layout);

        usernamePromptTextView = findViewById(R.id.register_username_activity_prompt_text_view);
        usernameTextInputLayout = findViewById(R.id.register_username_activity_text_input_layout);
        usernameEditText = findViewById(R.id.register_username_activity_edit_text);
        nextButton = findViewById(R.id.register_username_activity_next_button);

        usernamePromptTextView.setTypeface(Default.sourceSansProLight);
        usernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        usernameEditText.setTypeface(Default.sourceSansProLight);

        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");

        usernameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = ProfileHelper.getFormattedUsername(usernameEditText);
                String username = ProfileHelper.getFirebaseEncodedUsername(inputUsername);
                createPrismUser(username);
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
                        String username = ProfileHelper.getFirebaseEncodedUsername(inputUsername);
                        createPrismUser(username);
                        return true;
                }
                return false;
            }
        });
    }

    public void createPrismUser(String username) {

        if (ProfileHelper.isUsernameValid(username, usernameTextInputLayout)) {
            FirebaseProfileAction.isUsernameTaken(username, new OnUsernameTakenCallback() {
                @Override
                public void onSuccess(boolean usernameTaken) {
                    if (usernameTaken) {
                        usernameTextInputLayout.setError("Username is taken");
                    } else {
                        FirebaseProfileAction.createPrismUserInFirebase(CurrentUser.getFirebaseUser(), fullName, username, new OnPrismUserRegistrationCallback() {
                            @Override
                            public void onSuccess() {
                                SHOULD_SIGN_OUT = Boolean.FALSE;
                                intentToMainActivity();
                            }
                        });
                    }
                }

                @Override
                public void onFailure() {
                    // TODO Log this
                }
            });
        }
    }

    /**
     * Intent to Main Activity from Login Activity
     * TODO Rename this method
     */
    private void intentToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CurrentUser.prepareAppForUser(this, intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        if (SHOULD_SIGN_OUT) {
            Helper.toast(this, "Failed to sign in with Google");
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        super.onPause();
    }

 }
