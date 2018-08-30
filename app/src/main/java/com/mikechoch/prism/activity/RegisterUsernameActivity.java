package com.mikechoch.prism.activity;

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

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.ProfileHelper;

public class RegisterUsernameActivity extends AppCompatActivity {

    private TextView usernamePromptTextView;
    private TextInputLayout usernameTextInputLayout;
    private EditText usernameEditText;
    private Button nextButton;

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

        usernameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                if (ProfileHelper.isUsernameValid(username, usernameTextInputLayout)) {
                    //TODO: Go to MainActivity
                }
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
                        ProfileHelper.isFullNameValid(s.toString().trim(), usernameTextInputLayout);
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
                        String username = usernameEditText.getText().toString().trim();
                        if (ProfileHelper.isFullNameValid(username, usernameTextInputLayout)) {
                            //TODO: Go to MainActivity
                        }
                        return true;
                }
                return false;
            }
        });
    }

}
