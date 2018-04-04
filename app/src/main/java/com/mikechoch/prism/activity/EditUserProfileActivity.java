package com.mikechoch.prism.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;
import com.mikechoch.prism.R;
import com.mikechoch.prism.fire.CurrentUser;

/**
 * Created by mikechoch on 2/18/18.
 */

public class EditUserProfileActivity extends AppCompatActivity {

    /*
     * Globals
     */
    private Typeface sourceSansProLight;
    private Typeface sourceSansProBold;
    private int screenWidth;
    private int screenHeight;

    private Toolbar toolbar;
    private TextView toolbarTextView;

    private ImageView iconImageView;
    private TextInputLayout fullNameTextInputLayout;
    private EditText fullNameEditText;
    private TextInputLayout usernameTextInputLayout;
    private EditText usernameEditText;
    private TextInputLayout emailTextInputLayout;
    private EditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;
    private Button editAccountButton;
    private ProgressBar editAccountProgressBar;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.expense_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile_activity_layout);

        // Create two typefaces
        sourceSansProLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        sourceSansProBold = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Black.ttf");

        // Get the screen width and height of the current phone
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);
        fullNameTextInputLayout = findViewById(R.id.edit_account_name_text_input_layout);
        fullNameEditText = findViewById(R.id.edit_account_name_edit_text);
        usernameTextInputLayout = findViewById(R.id.edit_account_username_text_input_layout);
        usernameEditText = findViewById(R.id.edit_account_username_edit_text);
        passwordTextInputLayout = findViewById(R.id.edit_account_password_text_input_layout);
        passwordEditText = findViewById(R.id.edit_account_password_edit_text);
        emailTextInputLayout = findViewById(R.id.edit_account_email_text_input_layout);
        emailEditText = findViewById(R.id.edit_account_email_edit_text);
        editAccountButton = findViewById(R.id.edit_account_submit_button);
        editAccountProgressBar = findViewById(R.id.edit_account_progress_bar);

        passwordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click");
                CustomAlertDialogBuilder changePasswordAlertDialog = createSetPasswordAlertDialog();
                changePasswordAlertDialog.show();
            }
        });

        setupUIElements();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Create an AlertDialog for when the user clicks the password edit text
     * This will ask the user to enter the old password and new password
     * Click OK to verify and update the password
     */
    private CustomAlertDialogBuilder createSetPasswordAlertDialog() {
        View changePasswordView = getLayoutInflater().inflate(R.layout.change_password_alert_dialog_layout, null);
        RelativeLayout changePasswordRelativeLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_relative_layout);

        TextInputLayout oldPasswordTextInputLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_old_password_text_input_layout);
        EditText oldPasswordEditText = changePasswordView.findViewById(R.id.change_password_alert_dialog_old_password_edit_text);
        TextInputLayout newPasswordTextInputLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_new_password_text_input_layout);
        EditText newPasswordEditText = changePasswordView.findViewById(R.id.change_password_alert_dialog_new_password_edit_text);
        ProgressBar changePasswordProgressBar = changePasswordView.findViewById(R.id.change_password_progress_bar);

        oldPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        oldPasswordTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);
        newPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        newPasswordTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);

        oldPasswordTextInputLayout.setTypeface(sourceSansProLight);
        oldPasswordEditText.setTypeface(sourceSansProLight);
        newPasswordTextInputLayout.setTypeface(sourceSansProLight);
        newPasswordEditText.setTypeface(sourceSansProLight);

        CustomAlertDialogBuilder profilePictureAlertDialog = new CustomAlertDialogBuilder(this, changePasswordRelativeLayout);
        profilePictureAlertDialog.setView(changePasswordRelativeLayout);
        profilePictureAlertDialog.setIsCancelable(true);
        profilePictureAlertDialog.setCanceledOnTouchOutside(false);
        profilePictureAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changePasswordProgressBar.setVisibility(View.VISIBLE);

                oldPasswordTextInputLayout.setEnabled(false);
                oldPasswordEditText.setEnabled(false);
                newPasswordTextInputLayout.setEnabled(false);
                newPasswordEditText.setEnabled(false);
                profilePictureAlertDialog.getPositiveButtonElement().setEnabled(false);
                profilePictureAlertDialog.getNegativeButtonElement().setEnabled(false);

                profilePictureAlertDialog.setIsCancelable(false);
            }
        }).setNegativeButton("CANCEL", null
        ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        return profilePictureAlertDialog;
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();

        // Setup Typefaces for all text based UI elements
        toolbarTextView.setTypeface(sourceSansProLight);
        fullNameTextInputLayout.setTypeface(sourceSansProLight);
        fullNameEditText.setTypeface(sourceSansProLight);
        usernameTextInputLayout.setTypeface(sourceSansProLight);
        usernameEditText.setTypeface(sourceSansProLight);
        passwordTextInputLayout.setTypeface(sourceSansProLight);
        passwordEditText.setTypeface(sourceSansProLight);
        emailTextInputLayout.setTypeface(sourceSansProLight);
        emailEditText.setTypeface(sourceSansProLight);
        editAccountButton.setTypeface(sourceSansProLight);

        fullNameEditText.setText(CurrentUser.prismUser.getFullName());
        usernameEditText.setText(CurrentUser.prismUser.getUsername());
        passwordEditText.setText("********");
//        emailEditText.setText();

    }

}
