package com.mikechoch.prism.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;

public class EmailVerificationMessageActivity extends AppCompatActivity {

    Handler handler = new Handler();
    ImageView iconImageView;
    TextView welcomeTextView;
    TextView messageTextView;
    Button resendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification_message_activity);

        setupIconImageView();
        setupTextViews();
        setupResendEmailButton();

    }

    private void setupResendEmailButton() {
        resendEmailButton = findViewById(R.id.resend_verification_email_button);

        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.getFirebaseUser().sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                resendEmailButton.setVisibility(View.GONE);
                                RelativeLayout view = findViewById(R.id.email_verification_relative_layout);
                                Snackbar.make(view, "Email sent again", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                // TODO Log this (display toast or something)
                            }
                        });
            }
        });
    }

    private void setupTextViews() {
        welcomeTextView = findViewById(R.id.welcome_textView);
        messageTextView = findViewById(R.id.message_textview);

        welcomeTextView.setTypeface(Default.sourceSansProBold);
        messageTextView.setTypeface(Default.sourceSansProLight);
        messageTextView.setText("Thank you for registering. We have sent you a verification link to your email at " +
                CurrentUser.getFirebaseUser().getEmail() + ". Once you verify your account, you can start exploring and sharing your artwork.");
    }

    private void initiateHandlerToCheckVerification() {
        ConstraintLayout view = findViewById(R.id.email_verification_message_layout);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Helper.toast(EmailVerificationMessageActivity.this, "Checking...");
                CurrentUser.getFirebaseUser().reload();
                if (CurrentUser.getFirebaseUser().isEmailVerified()) {
                    Snackbar.make(view, "Email successfully verified", Snackbar.LENGTH_SHORT);
                    IntentHelper.intentToMainActivity(EmailVerificationMessageActivity.this, true);
                }
                handler.postDelayed(this, 1500);
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    private void setupIconImageView() {
        iconImageView = findViewById(R.id.mini_icon_image_view);
        iconImageView.getLayoutParams().width = (int) (Default.screenWidth * 0.25);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iconImageView.getLayoutParams();
        iconImageView.setLayoutParams(lp);
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


}