package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.callback.action.OnSendVerificationEmailCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;


public class EmailVerificationMessageActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private ImageView iconImageView;
    private TextView welcomeTextView;
    private TextView messageTextView;
    private Button resendEmailButton;
    private ConstraintLayout emailVerificationMessageConstraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification_message_activity);

        iconImageView = findViewById(R.id.email_verification_message_mini_icon_image_view);
        welcomeTextView = findViewById(R.id.email_verification_message_welcome_text_view);
        messageTextView = findViewById(R.id.email_verification_message_message_text_view);
        resendEmailButton = findViewById(R.id.email_verification_message_resend_verification_email_button);
        emailVerificationMessageConstraintLayout = findViewById(R.id.email_verification_message_layout);

        setupInterfaceElements();
    }

    @Override
    protected void onResume() {
        initiateHandlerToCheckVerification();
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     *
     */
    private void setupResendEmailButton() {
        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSendVerificationEmail();
            }
        });
    }

    /**
     *
     */
    private void attemptSendVerificationEmail() {
        DatabaseAction.sendVerificationEmail(CurrentUser.getFirebaseUser(), new OnSendVerificationEmailCallback() {
            @Override
            public void onSuccess() {
                resendEmailButton.setVisibility(View.GONE);
                RelativeLayout view = findViewById(R.id.email_verification_relative_layout);
                Snackbar.make(view, "Email sent again", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                // TODO Log this (display toast or something)
            }
        });
    }

    /**
     *
     */
    private void setupTextViews() {
        String emailVerificationMessageString = "Thank you for registering. We have sent you a " +
                "verification link to your email at " + CurrentUser.getFirebaseUser().getEmail() +
                ". Once you verify your account, you can start exploring and sharing your artwork.";
        messageTextView.setText(emailVerificationMessageString);
    }

    /**
     *
     */
    private void initiateHandlerToCheckVerification() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Helper.toast(EmailVerificationMessageActivity.this, "Checking...");
                CurrentUser.getFirebaseUser().reload();
                if (CurrentUser.getFirebaseUser().isEmailVerified()) {
                    Snackbar.make(emailVerificationMessageConstraintLayout, "Email successfully verified", Snackbar.LENGTH_SHORT);
                    IntentHelper.intentToMainActivity(EmailVerificationMessageActivity.this, true);
                }
                handler.postDelayed(this, 1500);
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    /**
     *
     */
    private void setupIconImageView() {
        iconImageView.getLayoutParams().width = (int) (Default.screenWidth * 0.25);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iconImageView.getLayoutParams();
        iconImageView.setLayoutParams(lp);
    }

    /**
     *
     */
    private void setupInterfaceElements() {
        welcomeTextView.setTypeface(Default.sourceSansProBold);
        messageTextView.setTypeface(Default.sourceSansProLight);

        setupIconImageView();
        setupTextViews();
        setupResendEmailButton();
    }
}