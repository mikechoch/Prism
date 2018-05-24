package com.mikechoch.prism.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.ProfileHelper;
import com.mikechoch.prism.user_interface.CustomAlertDialogBuilder;

import java.util.HashMap;

/**
 * Created by mikechoch on 2/18/18.
 */

public class EditUserProfileActivity extends AppCompatActivity {

    /*
     * Globals
     */
    private DatabaseReference currentUserReference;

    private Toolbar toolbar;
    private TextView toolbarTextView;

    private TextInputLayout fullNameTextInputLayout;
    private EditText fullNameEditText;
    private TextInputLayout usernameTextInputLayout;
    private EditText usernameEditText;
    private TextInputLayout emailTextInputLayout;
    private EditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;

    private TextInputLayout fullNameAlertDialogTextInputLayout;
    private EditText fullNameAlertDialogEditText;
    private TextInputLayout usernameAlertDialogTextInputLayout;
    private EditText usernameAlertDialogEditText;
    private TextInputLayout oldPasswordAlertDialogTextInputLayout;
    private EditText oldPasswordAlertDialogEditText;
    private TextInputLayout newPasswordAlertDialogTextInputLayout;
    private EditText newPasswordAlertDialogEditText;
    private TextInputLayout oldEmailAlertDialogTextInputLayout;
    private EditText oldEmailAlertDialogEditText;
    private TextInputLayout newEmailAlertDialogTextInputLayout;
    private EditText newEmailAlertDialogEditText;
    private TextInputLayout passwordAlertDialogTextInputLayout;
    private EditText passwordAlertDialogEditText;

    private CustomAlertDialogBuilder changeFullNameAlertDialog;
    private CustomAlertDialogBuilder changeUsernameAlertDialog;
    private CustomAlertDialogBuilder changePasswordAlertDialog;
    private CustomAlertDialogBuilder changeEmailAlertDialog;

    private ProgressBar changeFullNameAlertDialogProgressBar;
    private ProgressBar changeUsernameAlertDialogProgressBar;
    private ProgressBar changePasswordAlertDialogProgressBar;
    private ProgressBar changeEmailAlertDialogProgressBar;


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

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);

        // Initialize all UI elements
        fullNameTextInputLayout = findViewById(R.id.edit_account_full_name_text_input_layout);
        fullNameEditText = findViewById(R.id.edit_account_full_name_edit_text);
        usernameTextInputLayout = findViewById(R.id.edit_account_username_text_input_layout);
        usernameEditText = findViewById(R.id.edit_account_username_edit_text);
        passwordTextInputLayout = findViewById(R.id.edit_account_password_text_input_layout);
        passwordEditText = findViewById(R.id.edit_account_password_edit_text);
        emailTextInputLayout = findViewById(R.id.edit_account_email_text_input_layout);
        emailEditText = findViewById(R.id.edit_account_email_edit_text);

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

        fullNameAlertDialogTextInputLayout = changeFullNameView.findViewById(R.id.change_full_name_alert_dialog_full_name_text_input_layout);
        fullNameAlertDialogEditText = changeFullNameView.findViewById(R.id.change_full_name_alert_dialog_full_name_edit_text);
        changeFullNameAlertDialogProgressBar = changeFullNameView.findViewById(R.id.change_full_name_progress_bar);

        fullNameAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        fullNameAlertDialogEditText.setTypeface(Default.sourceSansProLight);

        String oldFullName = this.fullNameEditText.getText().toString();
        fullNameEditText.setText(oldFullName);
        fullNameEditText.setSelection(oldFullName.length());
        fullNameAlertDialogEditText.setText(oldFullName);
        fullNameAlertDialogEditText.setSelection(oldFullName.length());
        //TODO: Add TextWatcher and error checking here for newFullNameEditText

        changeFullNameAlertDialog = new CustomAlertDialogBuilder(this, changeFullNameRelativeLayout);
        changeFullNameAlertDialog.setView(changeFullNameRelativeLayout);
        changeFullNameAlertDialog.setIsCancelable(true);
        changeFullNameAlertDialog.setCanceledOnTouchOutside(false);
        changeFullNameAlertDialog.setPositiveButton(Default.BUTTON_UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newFullName = fullNameAlertDialogEditText.getText().toString().trim();
                if (newFullName.equals(oldFullName)) {
                    dialog.dismiss();
                    return;
                }
                if (ProfileHelper.isFullNameValid(newFullName, fullNameAlertDialogTextInputLayout)) {
                    changeFullNameAlertDialogProgressBar.setVisibility(View.VISIBLE);
                    fullNameAlertDialogTextInputLayout.setEnabled(false);
                    fullNameAlertDialogEditText.setEnabled(false);
                    changeFullNameAlertDialog.getPositiveButtonElement().setEnabled(false);
                    changeFullNameAlertDialog.getNegativeButtonElement().setEnabled(false);
                    changeFullNameAlertDialog.setIsCancelable(false);

                    updateFullName(newFullName, dialog);
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

        usernameAlertDialogTextInputLayout = changeUsernameView.findViewById(R.id.change_username_alert_dialog_username_text_input_layout);
        usernameAlertDialogEditText = changeUsernameView.findViewById(R.id.change_username_alert_dialog_username_edit_text);
        changeUsernameAlertDialogProgressBar = changeUsernameView.findViewById(R.id.change_username_progress_bar);

        usernameAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        usernameAlertDialogEditText.setTypeface(Default.sourceSansProLight);

        String oldUsername = this.usernameEditText.getText().toString();
        usernameAlertDialogEditText.setText(oldUsername);
        usernameAlertDialogEditText.setSelection(oldUsername.length());

        //TODO: Add TextWatcher and error checking here for usernameEditText

        changeUsernameAlertDialog = new CustomAlertDialogBuilder(this, changeUsernameRelativeLayout);
        changeUsernameAlertDialog.setView(changeUsernameRelativeLayout);
        changeUsernameAlertDialog.setIsCancelable(true);
        changeUsernameAlertDialog.setCanceledOnTouchOutside(false);
        changeUsernameAlertDialog.setPositiveButton(Default.BUTTON_UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = usernameAlertDialogEditText.getText().toString().trim();
                if (newUsername.equals(oldUsername)) {
                    dialog.dismiss();
                    return;
                }
                if (ProfileHelper.isUsernameValid(newUsername, usernameAlertDialogTextInputLayout)) {
                    toggleUsernameAlertDialogAttributes(true);
                    updateUsername(oldUsername, newUsername, dialog, usernameAlertDialogTextInputLayout);
                }
            }
        });
        changeUsernameAlertDialog.setNegativeButton(Default.BUTTON_CANCEL, null);
        changeUsernameAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) { }
        });
        changeUsernameAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) { }
        });

        return changeUsernameAlertDialog;
    }

    /**
     *
     * @param disableAttributes
     */
    private void toggleUsernameAlertDialogAttributes(boolean disableAttributes) {
        int visibility = disableAttributes ? View.VISIBLE : View.GONE;
        changeUsernameAlertDialogProgressBar.setVisibility(visibility);
        usernameAlertDialogTextInputLayout.setEnabled(!disableAttributes);
        usernameAlertDialogEditText.setEnabled(!disableAttributes);
        changeUsernameAlertDialog.getPositiveButtonElement().setEnabled(!disableAttributes);
        changeUsernameAlertDialog.getNegativeButtonElement().setEnabled(!disableAttributes);
        changeUsernameAlertDialog.setIsCancelable(!disableAttributes);
    }

    /**
     * Create an AlertDialog for when the user clicks the password edit text
     * This will ask the user to enter the old password and new password
     * Click OK to verify and update the password
     */
    private CustomAlertDialogBuilder createSetPasswordAlertDialog() {
        View changePasswordView = getLayoutInflater().inflate(R.layout.change_password_alert_dialog_layout, null);
        RelativeLayout changePasswordRelativeLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_relative_layout);

        oldPasswordAlertDialogTextInputLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_old_password_text_input_layout);
        oldPasswordAlertDialogEditText = changePasswordView.findViewById(R.id.change_password_alert_dialog_old_password_edit_text);
        newPasswordAlertDialogTextInputLayout = changePasswordView.findViewById(R.id.change_password_alert_dialog_new_password_text_input_layout);
        newPasswordAlertDialogEditText = changePasswordView.findViewById(R.id.change_password_alert_dialog_new_password_edit_text);
        changePasswordAlertDialogProgressBar = changePasswordView.findViewById(R.id.change_password_progress_bar);

        oldPasswordAlertDialogTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        oldPasswordAlertDialogTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);
        newPasswordAlertDialogTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        newPasswordAlertDialogTextInputLayout.getPasswordVisibilityToggleDrawable().setTint(Color.WHITE);

        oldPasswordAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        oldPasswordAlertDialogEditText.setTypeface(Default.sourceSansProLight);
        newPasswordAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        newPasswordAlertDialogEditText.setTypeface(Default.sourceSansProLight);

        changePasswordAlertDialog = new CustomAlertDialogBuilder(this, changePasswordRelativeLayout);
        changePasswordAlertDialog.setView(changePasswordRelativeLayout);
        changePasswordAlertDialog.setIsCancelable(true);
        changePasswordAlertDialog.setCanceledOnTouchOutside(false);
        changePasswordAlertDialog.setPositiveButton(Default.BUTTON_UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: add error checking for Old Password
                String oldPassword = oldPasswordAlertDialogEditText.getText().toString().trim();
                String newPassword = newPasswordAlertDialogEditText.getText().toString().trim();
                if (oldPassword.equals(newPassword)) {
                    dialog.dismiss();
                    return;
                }
                if (ProfileHelper.isPasswordValid(newPassword, newPasswordAlertDialogTextInputLayout)) {
                    togglePasswordAlertDialogAttributes(true);
                    updatePassword(oldPassword, newPassword, dialog);
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

        return changePasswordAlertDialog;
    }

    /**
     *
     * @param disableAttributes
     */
    private void togglePasswordAlertDialogAttributes(boolean disableAttributes) {
        int visibility = disableAttributes ? View.VISIBLE : View.GONE;
        changePasswordAlertDialogProgressBar.setVisibility(visibility);
        oldPasswordAlertDialogTextInputLayout.setEnabled(!disableAttributes);
        oldPasswordAlertDialogEditText.setEnabled(!disableAttributes);
        newPasswordAlertDialogTextInputLayout.setEnabled(!disableAttributes);
        newPasswordAlertDialogEditText.setEnabled(!disableAttributes);
        changePasswordAlertDialog.getPositiveButtonElement().setEnabled(!disableAttributes);
        changePasswordAlertDialog.getNegativeButtonElement().setEnabled(!disableAttributes);
        changePasswordAlertDialog.setIsCancelable(!disableAttributes);
    }
    
    private void toggleEmailAlertDialogAttributes(boolean disableAttributes) {
        int visibility = disableAttributes ? View.VISIBLE : View.GONE;
        changeEmailAlertDialogProgressBar.setVisibility(visibility);
        oldEmailAlertDialogEditText.setEnabled(!disableAttributes);
        oldEmailAlertDialogEditText.setEnabled(!disableAttributes);
        newEmailAlertDialogTextInputLayout.setEnabled(!disableAttributes);
        newEmailAlertDialogTextInputLayout.setErrorEnabled(!disableAttributes);
        newEmailAlertDialogEditText.setEnabled(!disableAttributes);
        passwordAlertDialogTextInputLayout.setEnabled(!disableAttributes);
        passwordAlertDialogTextInputLayout.setErrorEnabled(!disableAttributes);
        passwordAlertDialogEditText.setEnabled(!disableAttributes);
        changeEmailAlertDialog.getPositiveButtonElement().setEnabled(!disableAttributes);
        changeEmailAlertDialog.getNegativeButtonElement().setEnabled(!disableAttributes);
        changeEmailAlertDialog.setIsCancelable(!disableAttributes);
    }

    /**
     *
     */
    private CustomAlertDialogBuilder createSetEmailAlertDialog() {
        View changeEmailView = getLayoutInflater().inflate(R.layout.change_email_alert_dialog_layout, null);
        RelativeLayout changeEmailRelativeLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_relative_layout);

        oldEmailAlertDialogTextInputLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_old_email_text_input_layout);
        oldEmailAlertDialogEditText = changeEmailView.findViewById(R.id.change_email_alert_dialog_old_email_edit_text);
        newEmailAlertDialogTextInputLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_new_email_text_input_layout);
        newEmailAlertDialogEditText = changeEmailView.findViewById(R.id.change_email_alert_dialog_new_email_edit_text);
        passwordAlertDialogTextInputLayout = changeEmailView.findViewById(R.id.change_email_alert_dialog_password_text_input_layout);
        passwordAlertDialogEditText = changeEmailView.findViewById(R.id.change_email_alert_dialog_password_email_edit_text);
        changeEmailAlertDialogProgressBar = changeEmailView.findViewById(R.id.change_email_progress_bar);

        oldEmailAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        oldEmailAlertDialogEditText.setTypeface(Default.sourceSansProLight);
        newEmailAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        newEmailAlertDialogEditText.setTypeface(Default.sourceSansProLight);
        passwordAlertDialogTextInputLayout.setTypeface(Default.sourceSansProLight);
        passwordAlertDialogEditText.setTypeface(Default.sourceSansProLight);

        String oldEmail = this.emailEditText.getText().toString();
        oldEmailAlertDialogEditText.setText(oldEmail);
        oldEmailAlertDialogEditText.setSelection(oldEmail.length());

        //TODO: Add TextWatcher and error checking here for emailEditText

        changeEmailAlertDialog = new CustomAlertDialogBuilder(this, changeEmailRelativeLayout);
        changeEmailAlertDialog.setView(changeEmailRelativeLayout);
        changeEmailAlertDialog.setIsCancelable(true);
        changeEmailAlertDialog.setCanceledOnTouchOutside(false);
        changeEmailAlertDialog.setPositiveButton(Default.BUTTON_UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: add error checking for Old Password
                String password = passwordAlertDialogEditText.getText().toString().trim();
                String newEmail = newEmailAlertDialogEditText.getText().toString().trim();
                toggleEmailAlertDialogAttributes(true);
                updateEmail(newEmail, password, dialog);

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
        toolbarTextView.setTypeface(Default.sourceSansProLight);
        fullNameTextInputLayout.setTypeface(Default.sourceSansProLight);
        fullNameEditText.setTypeface(Default.sourceSansProLight);
        usernameTextInputLayout.setTypeface(Default.sourceSansProLight);
        usernameEditText.setTypeface(Default.sourceSansProLight);
        passwordTextInputLayout.setTypeface(Default.sourceSansProLight);
        passwordEditText.setTypeface(Default.sourceSansProLight);
        emailTextInputLayout.setTypeface(Default.sourceSansProLight);
        emailEditText.setTypeface(Default.sourceSansProLight);

        fullNameEditText.setText(CurrentUser.prismUser.getFullName());
        usernameEditText.setText(CurrentUser.prismUser.getUsername());
        passwordEditText.setText(Default.HIDDEN_PASSWORD);
        emailEditText.setText(CurrentUser.firebaseUser.getEmail());
    }

    /**
     *
     * @param newFullName
     * @param dialog
     */
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
                            Helper.toast(EditUserProfileActivity.this, Message.FULL_NAME_UPDATE_SUCCESS);

                        } else {
                            Log.e(Default.TAG_DB, Message.FULL_NAME_UPDATE_FAIL, task.getException());
                            Helper.toast(EditUserProfileActivity.this, Message.FULL_NAME_UPDATE_FAIL);
                        }
                        dialog.dismiss();
                    }
                });


    }

    private void updateUsername(String oldUsername, String newUsername, DialogInterface dialog, TextInputLayout usernameTextInputLayout) {
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
                    toggleUsernameAlertDialogAttributes(false);
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
                            Helper.toast(EditUserProfileActivity.this, Message.USERNAME_UPDATE_SUCCESS);
                        } else {
                            Log.e(Default.TAG_DB, Message.USERNAME_UPDATE_FAIL, task.getException());
                            Helper.toast(EditUserProfileActivity.this, Message.USERNAME_UPDATE_FAIL);
                        }
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    private void updatePassword(String oldPassword, String newPassword, DialogInterface dialog) {
        // TODO update in
        // 1) FirebaseUser.newPassword
        String email = CurrentUser.firebaseUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        CurrentUser.firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            CurrentUser.firebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CurrentUser.updateLocalCurrentUser();
                                                passwordEditText.setText(Default.HIDDEN_PASSWORD);
                                                Helper.toast(EditUserProfileActivity.this, Message.PASSWORD_UPDATE_SUCCESS);
                                            } else {
                                                Log.e(Default.TAG_DB, Message.PASSWORD_UPDATE_FAIL, task.getException());
                                                Helper.toast(EditUserProfileActivity.this, Message.PASSWORD_UPDATE_FAIL);
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                                    newPasswordAlertDialogTextInputLayout.setError("Password is too weak");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            Log.e(Default.TAG_DB, Message.REAUTH_FAIL);
                            togglePasswordAlertDialogAttributes(false);
                            oldPasswordAlertDialogTextInputLayout.setError("Incorrect password");
                        }
                    }
                });

    }

    private void updateEmail(String newEmail, String password, DialogInterface dialog) {
        // TODO check new email isn't taken
        // TODO update in
        // TODO ReAuthenticate and update CurrentUser.firebaseUser
        // 1) ACCOUNTS -> CurrentUser.username.value = newEmail
        // 2) FirebaseUser.newEmail
        String email = CurrentUser.firebaseUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        CurrentUser.firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (email.equals(newEmail)) {
                                dialog.dismiss();
                                return;
                            }
                            if (ProfileHelper.isEmailValid(newEmail, newEmailAlertDialogTextInputLayout)) {
                                CurrentUser.firebaseUser.updateEmail(newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference accountReference = Default.ACCOUNT_REFERENCE;
                                                    accountReference.child(CurrentUser.prismUser.getUsername()).setValue(newEmail)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                CurrentUser.updateLocalCurrentUser();
                                                                emailEditText.setText(newEmail);
                                                                Helper.toast(EditUserProfileActivity.this, Message.EMAIL_UPDATE_SUCCESS);
                                                            } else {
                                                                Helper.toast(EditUserProfileActivity.this, Message.EMAIL_UPDATE_FAIL);
                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                } else {
                                                    toggleEmailAlertDialogAttributes(false);
                                                    Log.e(Default.TAG_DB, Message.EMAIL_UPDATE_FAIL, task.getException());
                                                    Helper.toast(EditUserProfileActivity.this, Message.EMAIL_UPDATE_FAIL);
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                                        newEmailAlertDialogTextInputLayout.setError("Invalid email");
                                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                                        newEmailAlertDialogTextInputLayout.setError("Email already exists");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                        });
                            } else {
                                toggleEmailAlertDialogAttributes(false);
                            }
                        } else {
                            Log.e(Default.TAG_DB, Message.REAUTH_FAIL);
                            toggleEmailAlertDialogAttributes(false);
                            passwordAlertDialogTextInputLayout.setError("Incorrect password");
                        }
                    }
                });
    }

}
