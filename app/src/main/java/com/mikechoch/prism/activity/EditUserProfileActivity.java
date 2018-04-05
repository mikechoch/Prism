package com.mikechoch.prism.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.constants.Default;
import com.mikechoch.prism.constants.Key;
import com.mikechoch.prism.constants.Message;
import com.mikechoch.prism.helper.ProfileHelper;
import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;
import com.mikechoch.prism.R;
import com.mikechoch.prism.fire.CurrentUser;

import java.util.HashMap;

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
    private LinearLayout passwordLinearLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;
    private Button editAccountButton;
    private ProgressBar editAccountProgressBar;

    private DatabaseReference currentUserReference;


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

        currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());

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
        passwordLinearLayout = findViewById(R.id.edit_account_password_linear_layout);
        passwordTextInputLayout = findViewById(R.id.edit_account_password_text_input_layout);
        passwordEditText = findViewById(R.id.edit_account_password_edit_text);
        emailTextInputLayout = findViewById(R.id.edit_account_email_text_input_layout);
        emailEditText = findViewById(R.id.edit_account_email_edit_text);
        editAccountButton = findViewById(R.id.edit_account_submit_button);
        editAccountProgressBar = findViewById(R.id.edit_account_progress_bar);

        // Focusable is set false in XML for passwordEditText
        // When clicked an AlertDialog will be opened for changing the user's password
        fullNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogBuilder changeFullNameAlertDialog = createSetFullNameAlertDialog();
                changeFullNameAlertDialog.show();
            }
        });

        usernameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogBuilder changeUsernameAlertDialog = createSetUsernameAlertDialog();
                changeUsernameAlertDialog.show();
            }
        });

        passwordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogBuilder changePasswordAlertDialog = createSetPasswordAlertDialog();
                changePasswordAlertDialog.show();
            }
        });

        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogBuilder changeEmailAlertDialog = createSetEmailAlertDialog();
                changeEmailAlertDialog.show();
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
     *
     */
    private CustomAlertDialogBuilder createSetFullNameAlertDialog() {
        View changeFullNameView = getLayoutInflater().inflate(R.layout.change_full_name_alert_dialog_layout, null);
        RelativeLayout changeFullNameRelativeLayout = changeFullNameView.findViewById(R.id.change_full_name_alert_dialog_relative_layout);

        TextInputLayout fullNameTextInputLayout = changeFullNameView.findViewById(R.id.change_full_name_alert_dialog_full_name_text_input_layout);
        EditText newFullNameEditText = changeFullNameView.findViewById(R.id.change_full_name_alert_dialog_full_name_edit_text);
        ProgressBar changeFullNameProgressBar = changeFullNameView.findViewById(R.id.change_full_name_progress_bar);

        fullNameTextInputLayout.setTypeface(sourceSansProLight);
        newFullNameEditText.setTypeface(sourceSansProLight);

        String fullNameString = this.fullNameEditText.getText().toString();
        newFullNameEditText.setText(fullNameString);
        newFullNameEditText.setSelection(fullNameString.length());

        //TODO: Add TextWatcher and error checking here for newFullNameEditText

        CustomAlertDialogBuilder changeFullNameAlertDialog = new CustomAlertDialogBuilder(this, changeFullNameRelativeLayout);
        changeFullNameAlertDialog.setView(changeFullNameRelativeLayout);
        changeFullNameAlertDialog.setIsCancelable(true);
        changeFullNameAlertDialog.setCanceledOnTouchOutside(false);
        changeFullNameAlertDialog.setPositiveButton(Default.BUTTON_SAVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newFullName = newFullNameEditText.getText().toString().trim();
                if (!newFullName.equals(fullNameString) && ProfileHelper.isFullNameValid(newFullName, fullNameTextInputLayout)) {
                    changeFullNameProgressBar.setVisibility(View.VISIBLE);
                    fullNameTextInputLayout.setEnabled(false);
                    newFullNameEditText.setEnabled(false);
                    changeFullNameAlertDialog.getPositiveButtonElement().setEnabled(false);
                    changeFullNameAlertDialog.getNegativeButtonElement().setEnabled(false);
                    changeFullNameAlertDialog.setIsCancelable(false);

                    updateFullName(newFullName, dialog);
                } else {
                    dialog.dismiss();
                }



            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null
        ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) { }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) { }
        });
        return changeFullNameAlertDialog;
    }

    /**
     *
     */
    private CustomAlertDialogBuilder createSetUsernameAlertDialog() {
        View changeUsernameView = getLayoutInflater().inflate(R.layout.change_username_alert_dialog_layout, null);
        RelativeLayout changeUsernameRelativeLayout = changeUsernameView.findViewById(R.id.change_username_alert_dialog_relative_layout);

        TextInputLayout usernameTextInputLayout = changeUsernameView.findViewById(R.id.change_username_alert_dialog_username_text_input_layout);
        EditText usernameEditText = changeUsernameView.findViewById(R.id.change_username_alert_dialog_username_edit_text);
        ProgressBar changeUsernameProgressBar = changeUsernameView.findViewById(R.id.change_username_progress_bar);

        usernameTextInputLayout.setTypeface(sourceSansProLight);
        usernameEditText.setTypeface(sourceSansProLight);

        String usernameString = this.usernameEditText.getText().toString();
        usernameEditText.setText(usernameString);
        usernameEditText.setSelection(usernameString.length());

        //TODO: Add TextWatcher and error checking here for usernameEditText

        CustomAlertDialogBuilder changeUsernameAlertDialog = new CustomAlertDialogBuilder(this, changeUsernameRelativeLayout);
        changeUsernameAlertDialog.setView(changeUsernameRelativeLayout);
        changeUsernameAlertDialog.setIsCancelable(true);
        changeUsernameAlertDialog.setCanceledOnTouchOutside(false);
        changeUsernameAlertDialog.setPositiveButton(Default.BUTTON_SAVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: add error checking for Old Password
                String newUsername = usernameEditText.getText().toString().trim();
                if (!newUsername.equals(usernameString) && ProfileHelper.isUsernameValid(newUsername, usernameTextInputLayout)) {
                    changeUsernameProgressBar.setVisibility(View.VISIBLE);
                    usernameTextInputLayout.setEnabled(false);
                    usernameEditText.setEnabled(false);
                    changeUsernameAlertDialog.getPositiveButtonElement().setEnabled(false);
                    changeUsernameAlertDialog.getNegativeButtonElement().setEnabled(false);
                    changeUsernameAlertDialog.setIsCancelable(false);

                    updateUsername(usernameString, newUsername, dialog);
                } else {
                    dialog.dismiss();
                }
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null
        ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        return changeUsernameAlertDialog;
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

        //TODO: Add TextWatcher and error checking here for passwords?????

        CustomAlertDialogBuilder changePasswordAlertDialog = new CustomAlertDialogBuilder(this, changePasswordRelativeLayout);
        changePasswordAlertDialog.setView(changePasswordRelativeLayout);
        changePasswordAlertDialog.setIsCancelable(true);
        changePasswordAlertDialog.setCanceledOnTouchOutside(false);
        changePasswordAlertDialog.setPositiveButton(Default.BUTTON_SAVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: add error checking for Old Password

                changePasswordProgressBar.setVisibility(View.VISIBLE);

                oldPasswordTextInputLayout.setEnabled(false);
                oldPasswordEditText.setEnabled(false);
                newPasswordTextInputLayout.setEnabled(false);
                newPasswordEditText.setEnabled(false);
                changePasswordAlertDialog.getPositiveButtonElement().setEnabled(false);
                changePasswordAlertDialog.getNegativeButtonElement().setEnabled(false);

                changePasswordAlertDialog.setIsCancelable(false);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null
        ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        return changePasswordAlertDialog;
    }

    /**
     *
     */
    private CustomAlertDialogBuilder createSetEmailAlertDialog() {
        View changeEmailView = getLayoutInflater().inflate(R.layout.change_email_alert_dialog_layout, null);
        RelativeLayout changeEmailRelativeLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_relative_layout);

        TextInputLayout emailTextInputLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_email_text_input_layout);
        EditText emailEditText = changeEmailView.findViewById(R.id.change_email_alert_dialog_email_edit_text);
        ProgressBar changeEmailProgressBar = changeEmailView.findViewById(R.id.change_email_progress_bar);

        emailTextInputLayout.setTypeface(sourceSansProLight);
        emailEditText.setTypeface(sourceSansProLight);

        String emailString = this.emailEditText.getText().toString();
        emailEditText.setText(emailString);
        emailEditText.setSelection(emailString.length());

        //TODO: Add TextWatcher and error checking here for emailEditText

        CustomAlertDialogBuilder changeEmailAlertDialog = new CustomAlertDialogBuilder(this, changeEmailRelativeLayout);
        changeEmailAlertDialog.setView(changeEmailRelativeLayout);
        changeEmailAlertDialog.setIsCancelable(true);
        changeEmailAlertDialog.setCanceledOnTouchOutside(false);
        changeEmailAlertDialog.setPositiveButton(Default.BUTTON_SAVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: add error checking for Old Password

                changeEmailProgressBar.setVisibility(View.VISIBLE);

                emailTextInputLayout.setEnabled(false);
                emailEditText.setEnabled(false);
                changeEmailAlertDialog.getPositiveButtonElement().setEnabled(false);
                changeEmailAlertDialog.getNegativeButtonElement().setEnabled(false);

                changeEmailAlertDialog.setIsCancelable(false);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, null
        ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        return changeEmailAlertDialog;
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
        emailEditText.setText(CurrentUser.firebaseUser.getEmail());

    }


    private void updateFullName(String newFullName, DialogInterface dialog) {
        // TODO update in
        // 1) USERS -> CurrentUser.uid -> "fullname"
        currentUserReference.child(Key.USER_PROFILE_FULL_NAME).setValue(newFullName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            fullNameEditText.setText(newFullName);
                            CurrentUser.prismUser.setFullName(newFullName);
                            toast(Message.FULL_NAME_UPDATE_SUCCESS);

                        } else {
                            Log.e(Default.TAG_DB, Message.FULL_NAME_UPDATE_FAIL, task.getException());
                            toast(Message.FULL_NAME_UPDATE_FAIL);
                        }
                        dialog.dismiss();
                    }
                });

        
    }

    private void updateUsername(String oldUsername, String newUsername, DialogInterface dialog) {
        // TODO check new username isn't taken
        // TODO update in
        // 1) ACCOUNTS -> CurrentUser.username
        // 2) USERS -> CurrentUser.uid -> "username"
        // 3) FirebaseUser.displayname

        String old_username_account_path = Key.DB_REF_ACCOUNTS + "/" + oldUsername;
        String new_username_account_path = Key.DB_REF_ACCOUNTS + "/" + newUsername;
        String new_username_user_path = Key.DB_REF_USER_PROFILES + "/" + CurrentUser.prismUser.getUid() + "/" + Key.USER_PROFILE_USERNAME;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference accountReference = Default.ACCOUNT_REFERENCE;
        accountReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(newUsername)) {
                    usernameTextInputLayout.setError("Username is taken. Try again");
                    return;
                }
                String email = (String) dataSnapshot.child(oldUsername).getValue();

                HashMap<String, Object> children = new HashMap<>();
                children.put(old_username_account_path, null);
                children.put(new_username_account_path, email);
                children.put(new_username_user_path, newUsername);

                databaseReference.updateChildren(children).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername).build();
                            CurrentUser.firebaseUser.updateProfile(profileUpdate);
                            CurrentUser.prismUser.setUsername(newUsername);
                            usernameEditText.setText(newUsername);
                            toast(Message.USERNAME_UPDATE_SUCCESS);
                        } else {
                            Log.e(Default.TAG_DB, Message.USERNAME_UPDATE_FAIL, task.getException());
                            toast(Message.USERNAME_UPDATE_FAIL);
                        }
                    }
                });
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    private void updatePassword(String newPassword) {
        // TODO update in
        // 1) FirebaseUser.newPassword
    }

    private void updateEmail(String newEmail) {
        // TODO check new email isn't taken
        // TODO update in
        // TODO ReAuthenticate
        // 1) ACCOUNTS -> CurrentUser.username.value = newEmail
        // 2) FirebaseUser.newEmail
    }

    /**
     * Shortcut for displaying a Toast message
     */
    private void toast(String bread) {
        Toast.makeText(this, bread, Toast.LENGTH_SHORT).show();
    }



}
