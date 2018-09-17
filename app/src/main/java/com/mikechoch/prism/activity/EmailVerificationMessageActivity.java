package com.mikechoch.prism.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.mikechoch.prism.R;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;

public class EmailVerificationMessageActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification_message_activity);

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
                 handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1500);

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