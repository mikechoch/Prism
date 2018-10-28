package com.mikechoch.prism.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.EditUserProfileActivity;
import com.mikechoch.prism.activity.LoginActivity;
import com.mikechoch.prism.activity.NotificationSettingsActivity;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.MoreOption;
import com.mikechoch.prism.type.ProfilePictureOption;
import com.mikechoch.prism.type.Setting;
import com.mikechoch.prism.user_interface.InterfaceAction;


public class OptionRecyclerViewAdapter extends RecyclerView.Adapter {

    private final int SETTING_ITEM_TYPE = 0;
    private final int MORE_OPTION_ITEM_TYPE = 1;
    private final int PROFILE_PICTURE_OPTION_ITEM_TYPE = 2;

    private Context context;
    private Object[] dataSet;
    private PrismPost prismPost;
    private PrismUser prismUser;
    private ImageView userProfilePicImageView;
    private AlertDialog moreOptionAlertDialog;


    // Setting constructor
    public OptionRecyclerViewAdapter(Context context, Object[] dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    // MoreOption constructor
    public OptionRecyclerViewAdapter(Context context, Object[] dataSet, PrismUser prismUser, ImageView userProfilePicImageView, AlertDialog moreOptionAlertDialog) {
        this.context = context;
        this.dataSet = dataSet;
        this.prismUser = prismUser;
        this.userProfilePicImageView = userProfilePicImageView;
        this.moreOptionAlertDialog = moreOptionAlertDialog;
    }

    // MoreOption constructor
    public OptionRecyclerViewAdapter(Context context, Object[] dataSet, PrismPost prismPost, boolean isCurrentUser, AlertDialog moreOptionAlertDialog) {
        this.context = context;
        this.dataSet = dataSet;
        this.prismPost = prismPost;
        this.moreOptionAlertDialog = moreOptionAlertDialog;

        // If not CurrentUser, re-create the more option without a delete option
        if (!isCurrentUser) {
            this.dataSet = new Object[]{dataSet[0], dataSet[1]};
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case SETTING_ITEM_TYPE:
                viewHolder = new SettingViewHolder(
                        LayoutInflater.from(context)
                                .inflate(R.layout.option_recycler_view_item_layout, parent, false));
                break;
            case MORE_OPTION_ITEM_TYPE:
                viewHolder = new MoreOptionViewHolder(
                        LayoutInflater.from(context)
                                .inflate(R.layout.option_recycler_view_item_layout, parent, false));
                break;
            case PROFILE_PICTURE_OPTION_ITEM_TYPE:
                viewHolder = new ProfilePictureOptionViewHolder(
                        LayoutInflater.from(context)
                                .inflate(R.layout.option_recycler_view_item_layout, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = dataSet[position];
        if (item instanceof Setting) {
            ((SettingViewHolder) holder).setData((Setting) item);
        } else if (item instanceof MoreOption) {
            ((MoreOptionViewHolder) holder).setData((MoreOption) item);
        } else if (item instanceof ProfilePictureOption) {
            ((ProfilePictureOptionViewHolder) holder).setData((ProfilePictureOption) item);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        int viewType = -1;
        Object item = dataSet[position];
        if (item instanceof Setting) {
            viewType = SETTING_ITEM_TYPE;
        } else if (item instanceof MoreOption) {
            viewType = MORE_OPTION_ITEM_TYPE;
        } else if (item instanceof ProfilePictureOption) {
            viewType = PROFILE_PICTURE_OPTION_ITEM_TYPE;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }


    public class SettingViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout settingsOptionRelativeLayout;
        private TextView settingsOptionTextView;
        private ImageView settingsOptionImageView;

        private Setting setting;


        SettingViewHolder(View itemView) {
            super(itemView);

            settingsOptionRelativeLayout = itemView.findViewById(R.id.settings_recycler_view_item_relative_layout);
            settingsOptionTextView = itemView.findViewById(R.id.settings_recycler_view_item_text_view);
            settingsOptionImageView = itemView.findViewById(R.id.settings_recycler_view_item_icon);
        }

        /**
         * Set data for the current SettingViewHolder elements
         */
        public void setData(Setting setting) {
            this.setting = setting;
            populateInterfaceElements();
        }

        /**
         * Handle SettingViewHolder click so that the appropriate action is taken
         * SETTINGS_OPTION_APP -
         * SETTINGS_OPTION_NOTIFICATION - Intent to NotificationSettingsActivity so user can change push notifications they receive
         * SETTINGS_OPTION_ACCOUNT - Intent to EditUserProfileActivity so user can change their user information
         * SETTINGS_OPTION_HELP -
         * SETTINGS_OPTION_ABOUT -
         * SETTINGS_OPTION_LOGOUT - Perform CurrentUser sign out and then Intent to LoginActivity
         */
        private void setupSettingsOptionRelativeLayout() {
            settingsOptionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int settingOptionId = setting.getId();
                    switch(settingOptionId) {
                        case Default.SETTINGS_OPTION_APP:
                            break;
                        case Default.SETTINGS_OPTION_NOTIFICATION:
                            IntentHelper.intentToNotificationSettingsActivity(context);
                            break;
                        case Default.SETTINGS_OPTION_ACCOUNT:
                            IntentHelper.intentToEditUserProfileActivity(context);
                            break;
                        case Default.SETTINGS_OPTION_HELP:
                            break;
                        case Default.SETTINGS_OPTION_ABOUT:
                            break;
                        case Default.SETTINGS_OPTION_LOGOUT:
                            CurrentUser.performSignOut();
                            IntentHelper.intentToLoginActivity(context);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        /**
         * Get the Setting enum title and populate the settingsOptionTextView
         */
        private void setupSettingsOptionTextView() {
            settingsOptionTextView.setText(setting.getTitle());
        }

        /**
         * Get the Setting enum icon and populate the profilePictureOptionImageView
         */
        private void setupSettingsOptionImageView() {
            Drawable settingsIcon = context.getResources().getDrawable(setting.getIcon());
            settingsIcon.setTint(Color.WHITE);
            settingsOptionImageView.setImageDrawable(settingsIcon);
        }

        /**
         * Populate all interface elements with data
         */
        private void populateInterfaceElements() {
            settingsOptionTextView.setTypeface(Default.sourceSansProLight);

            setupSettingsOptionRelativeLayout();
            setupSettingsOptionTextView();
            setupSettingsOptionImageView();
        }
    }


    public class MoreOptionViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout settingsOptionRelativeLayout;
        private TextView moreOptionsOptionTextView;
        private ImageView settingsOptionImageView;

        private MoreOption moreOption;


        MoreOptionViewHolder(View itemView) {
            super(itemView);

            settingsOptionRelativeLayout = itemView.findViewById(R.id.settings_recycler_view_item_relative_layout);
            moreOptionsOptionTextView = itemView.findViewById(R.id.settings_recycler_view_item_text_view);
            settingsOptionImageView = itemView.findViewById(R.id.settings_recycler_view_item_icon);
        }

        /**
         * Set data for the NotificationViewHolder interface elements
         */
        public void setData(MoreOption moreOption) {
            this.moreOption = moreOption;
            populateInterfaceElements();
        }

        /**
         * Setup the profilePictureOptionRelativeLayout onClickListener switch statement for each option
         */
        private void setupSettingsOptionRelativeLayout() {
            settingsOptionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreOptionAlertDialog != null) {
                        moreOptionAlertDialog.dismiss();
                    }
                    switch (moreOption.getId()) {
                        case Default.MORE_OPTION_REPORT:
                            // Report post
                            AlertDialog reportPostConfirmationAlertDialog = InterfaceAction.createReportPostConfirmationAlertDialog(context, prismPost);
                            reportPostConfirmationAlertDialog.show();
                            break;
                        case Default.MORE_OPTION_SHARE:
                            // Share
                            // TODO: Discuss what we should do about Share for now
                            break;
                        case Default.MORE_OPTION_DELETE:
                            // Delete
                            AlertDialog deleteConfirmationAlertDialog = InterfaceAction.createDeleteConfirmationAlertDialog(context, prismPost);
                            deleteConfirmationAlertDialog.show();
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        /**
         * Get the Setting enum title and populate the moreOptionsOptionTextView
         */
        private void setupSettingsOptionTextView() {
            moreOptionsOptionTextView.setText(moreOption.getTitle());
        }

        /**
         * Get the Setting enum icon and populate the profilePictureOptionImageView
         */
        private void setupSettingsOptionImageView() {
            Drawable settingsIcon = context.getResources().getDrawable(moreOption.getIcon());
            settingsIcon.setTint(Color.WHITE);
            settingsOptionImageView.setImageDrawable(settingsIcon);
        }

        /**
         * Populate all interface elements with data
         */
        private void populateInterfaceElements() {
            moreOptionsOptionTextView.setTypeface(Default.sourceSansProLight);

            setupSettingsOptionRelativeLayout();
            setupSettingsOptionTextView();
            setupSettingsOptionImageView();
        }
    }


    public class ProfilePictureOptionViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout profilePictureOptionRelativeLayout;
        private TextView profilePictureOptionTextView;
        private ImageView profilePictureOptionImageView;

        private ProfilePictureOption profilePictureOption;


        ProfilePictureOptionViewHolder(View itemView) {
            super(itemView);

            profilePictureOptionRelativeLayout = itemView.findViewById(R.id.settings_recycler_view_item_relative_layout);
            profilePictureOptionTextView = itemView.findViewById(R.id.settings_recycler_view_item_text_view);
            profilePictureOptionImageView = itemView.findViewById(R.id.settings_recycler_view_item_icon);
        }

        /**
         * Set data for the PrismPostViewHolder interface elements
         */
        public void setData(ProfilePictureOption profilePictureOption) {
            this.profilePictureOption = profilePictureOption;
            populateInterfaceElements();
        }

        /**
         * Setup the profilePictureOptionRelativeLayout onClickListener switch statement for each Setting
         */
        private void setupProfilePictureOptionRelativeLayout() {
            profilePictureOptionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int profilePictureOptionId = profilePictureOption.getId();
                    if (moreOptionAlertDialog != null) {
                        moreOptionAlertDialog.dismiss();
                    }
                    switch(profilePictureOptionId) {
                        case Default.PROFILE_PICTURE_GALLERY:
                            IntentHelper.intentToProfilePictureUploadActivity(context, Default.PROFILE_PICTURE_GALLERY);
                            break;
                        case Default.PROFILE_PICTURE_SELFIE:
                            IntentHelper.intentToProfilePictureUploadActivity(context, Default.PROFILE_PICTURE_SELFIE);
                            break;
                        case Default.PROFILE_PICTURE_VIEW:
                            IntentHelper.intentToShowUserProfilePictureActivity(context, prismUser, userProfilePicImageView);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        /**
         * Get the Setting enum title and populate the moreOptionsOptionTextView
         */
        private void setupProfilePictureOptionTextView() {
            profilePictureOptionTextView.setText(profilePictureOption.getTitle());
        }

        /**
         * Get the Setting enum icon and populate the profilePictureOptionImageView
         */
        private void setupProfilePictureOptionImageView() {
            Drawable profilePictureOptionIcon = context.getResources().getDrawable(profilePictureOption.getIcon());
            profilePictureOptionIcon.setTint(Color.WHITE);
            profilePictureOptionImageView.setImageDrawable(profilePictureOptionIcon);
        }

        /**
         * Populate all interface elements with data
         */
        private void populateInterfaceElements() {
            profilePictureOptionTextView.setTypeface(Default.sourceSansProLight);

            setupProfilePictureOptionRelativeLayout();
            setupProfilePictureOptionTextView();
            setupProfilePictureOptionImageView();
        }
    }
}