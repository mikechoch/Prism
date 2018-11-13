package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PostBasedNotification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.UserBasedNotification;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.NotificationType;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int POST_BASED_NOTIFICATION_TYPE = 0;
    private int USER_BASED_NOTIFICATION_TYPE = 1;

    private Context context;
    private ArrayList<Notification> notificationArrayList;


    public NotificationRecyclerViewAdapter(Context context, ArrayList<Notification> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        Notification notification = notificationArrayList.get(position);
        if (notification instanceof PostBasedNotification) {
            viewType = POST_BASED_NOTIFICATION_TYPE;
        } else if (notification instanceof UserBasedNotification) {
            viewType = USER_BASED_NOTIFICATION_TYPE;
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == POST_BASED_NOTIFICATION_TYPE) {
            viewHolder = new PrismPostNotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.prism_post_notification_item_layout, parent, false));
        } else if (viewType == USER_BASED_NOTIFICATION_TYPE) {
            return new PrismUserNotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.prism_user_notification_item_layout, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PrismPostNotificationViewHolder) {
            ((PrismPostNotificationViewHolder) holder).setData((PostBasedNotification) notificationArrayList.get(position));
        } else if (holder instanceof PrismUserNotificationViewHolder) {
            ((PrismUserNotificationViewHolder) holder).setData((UserBasedNotification) notificationArrayList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }


    public class PrismPostNotificationViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout notificationRelativeLayout;
        private ImageView userProfilePicImageView;
        private ImageView notificationTypeImageView;
        private TextView notificationDescriptionTextView;
        private TextView notificationTypeTextView;
        private ImageView prismPostThumbnailImageView;

        private PostBasedNotification postBasedNotification;


        PrismPostNotificationViewHolder(View itemView) {
            super(itemView);

            notificationRelativeLayout = itemView.findViewById(R.id.notification_prism_post_relative_layout);
            userProfilePicImageView = itemView.findViewById(R.id.notification_prism_post_profile_picture_image_view);
            notificationTypeImageView = itemView.findViewById(R.id.notification_prism_post_type_image_view);
            notificationDescriptionTextView = itemView.findViewById(R.id.notification_prism_post_description_text_view);
            notificationTypeTextView = itemView.findViewById(R.id.notification_prism_post_type_text_view);
            prismPostThumbnailImageView = itemView.findViewById(R.id.notification_prism_post_thumbnail_image_view);
        }

        /**
         * Set data the data for the current PrismPostNotificationViewHolder elements
         */
        public void setData(PostBasedNotification postBasedNotification) {
            this.postBasedNotification = postBasedNotification;

            populateInterfaceElements();
        }

        /**
         * Populate elements in current PrismPostNotificationViewHolder
         */
        private void populateInterfaceElements() {
            // Setup Typefaces for all text based UI elements
            notificationTypeTextView.setTypeface(Default.sourceSansProLight);
            notificationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToPrismPostDetailActivity(context, postBasedNotification.getPrismPost());
                }
            });

            constructNotificationTextViews();
            populateNotificationInfoFields();
            populateProfilePic();

            prismPostThumbnailImageView.setImageDrawable(null);
            if (!postBasedNotification.getNotificationType().equals(NotificationType.FOLLOW)) {
                populatePrismPostThumbnail();
            }
        }

        /**
         * Populates the notificationDescriptionLinearLayout, notificationTypeTextView, and notificationTypeImageView
         * notificationDescriptionLinearLayout gets the user and # of others who performed the action
         * notificationTypeTextView gets the notification type and time since the action occurred
         * notificationTypeImageView gets the icon of the NotificationType
         */
        private void populateNotificationInfoFields() {
            String notificationTypeAndTime = postBasedNotification.getNotificationType().toString() + " • " + Helper.getFancyDateDifferenceString(postBasedNotification.getActionTimestamp());
            notificationTypeTextView.setText(notificationTypeAndTime.toLowerCase());

            notificationTypeImageView.setImageDrawable(context.getResources().getDrawable(postBasedNotification.getNotificationType().getNotifIcon()));
        }

        /**
         * Construct the appropriate notification TextViews
         * Handles the string description of the user that performed the latest action and
         * how many others have also performed this action
         * ex. mikechoch and 2 others
         * This is generic enough to handle following, likes, or reposts
         */
        private void constructNotificationTextViews() {
            String mostRecentUsername = postBasedNotification.getMostRecentPrismUser().getUsername();
            TextView usernameTextView = new TextView(context);
            usernameTextView.setText(mostRecentUsername);
            usernameTextView.setTextSize(16);
            usernameTextView.setTextColor(Color.WHITE);
            Typeface notificationTypeface = postBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
            usernameTextView.setTypeface(notificationTypeface);
            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, postBasedNotification.getMostRecentPrismUser());
                }
            });

            Typeface typeface = postBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
            notificationDescriptionTextView.setTypeface(typeface);
            notificationDescriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            notificationDescriptionTextView.setHighlightColor(Color.TRANSPARENT);

            int otherCount = postBasedNotification.getOtherUserCount();
            String recentUsername = postBasedNotification.getMostRecentPrismUser().getUsername();
            if (otherCount > 0) {
                String userCountStringHead = " and " + String.valueOf(otherCount);
                String userCountStringTail = otherCount == 1 ? " other" : " others";
                String userCountString = recentUsername + userCountStringHead + userCountStringTail;
                SpannableString spannableString = Helper.createUsernameClickableSpan(context, postBasedNotification, userCountString, recentUsername.length());
                notificationDescriptionTextView.setText(spannableString);
            } else {
                SpannableString spannableString = Helper.createUsernameClickableSpan(context, postBasedNotification, recentUsername, recentUsername.length());
                notificationDescriptionTextView.setText(spannableString);
            }
        }

        /**
         * Using Glide populate the PrismPost ImageView
         * Validates that the notification has a PrismPost attached to it
         * When clicked will intent the user to the PrismPostDetailActivity of the PrismPost
         */
        private void populatePrismPostThumbnail() {
            PrismPost notificationPost = postBasedNotification.getPrismPost();
            if (notificationPost != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(notificationPost.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(prismPostThumbnailImageView);

                prismPostThumbnailImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToPrismPostDetailActivity(context, postBasedNotification.getPrismPost());
                    }
                });
            }
        }

        /**
         * Using Glide populate the profile picture of the person who performed the notification action
         * Validates that the PrismUser has a profile picture attached
         * When profile picture ImageView is clicked intent to UserProfileActivity of PrismUser
         */
        private void populateProfilePic() {
            PrismUser mostRecentPrismUser = postBasedNotification.getMostRecentPrismUser();
            if (mostRecentPrismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(mostRecentPrismUser.getProfilePicture().getLowResProfilePicUri())
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int imageViewPadding = (int) (1 * Default.scale);
                                RoundedBitmapDrawable profilePictureDrawable =
                                        BitmapHelper.createCircularProfilePicture(
                                                context,
                                                userProfilePicImageView,
                                                mostRecentPrismUser.getProfilePicture().isDefault(),
                                                resource,
                                                imageViewPadding);
                                userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                            }
                        });

                userProfilePicImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToUserProfileActivity(context, postBasedNotification.getMostRecentPrismUser());
                    }
                });
            }
        }
    }


    public class PrismUserNotificationViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout notificationRelativeLayout;
        private ImageView userProfilePicImageView;
        private ImageView notificationTypeImageView;
        private TextView notificationDescriptionTextView;
        private TextView notificationTypeTextView;

        private UserBasedNotification userBasedNotification;


        PrismUserNotificationViewHolder(View itemView) {
            super(itemView);

            notificationRelativeLayout = itemView.findViewById(R.id.notification_prism_user_relative_layout);
            userProfilePicImageView = itemView.findViewById(R.id.notification_prism_user_profile_picture_image_view);
            notificationTypeImageView = itemView.findViewById(R.id.notification_prism_user_type_image_view);
            notificationDescriptionTextView = itemView.findViewById(R.id.notification_prism_user_description_text_view);
            notificationTypeTextView = itemView.findViewById(R.id.notification_prism_user_type_text_view);
        }

        /**
         * Set data the data for the current PrismPostNotificationViewHolder elements
         */
        public void setData(UserBasedNotification userBasedNotification) {
            this.userBasedNotification = userBasedNotification;

            populateInterfaceElements();
        }

        /**
         * Populate elements in current PrismPostNotificationViewHolder
         */
        private void populateInterfaceElements() {
            Typeface typeface = userBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
            notificationDescriptionTextView.setTypeface(typeface);
            notificationTypeTextView.setTypeface(Default.sourceSansProLight);

            notificationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, userBasedNotification.getMostRecentPrismUser());
                }
            });

            constructNotificationTextViews();
            populateNotificationInfoFields();
            populateProfilePic();
        }

        /**
         * Populates the notificationDescriptionLinearLayout, notificationTypeTextView, and notificationTypeImageView
         * notificationDescriptionLinearLayout gets the user and # of others who performed the action
         * notificationTypeTextView gets the notification type and time since the action occurred
         * notificationTypeImageView gets the icon of the NotificationType
         */
        private void populateNotificationInfoFields() {
            String notificationTypeAndTime = userBasedNotification.getNotificationType().toString() + " • " + Helper.getFancyDateDifferenceString(userBasedNotification.getActionTimestamp());
            notificationTypeTextView.setText(notificationTypeAndTime.toLowerCase());

            notificationTypeImageView.setImageDrawable(context.getResources().getDrawable(userBasedNotification.getNotificationType().getNotifIcon()));
        }

        /**
         * Construct the appropriate notification TextViews
         * Handles the string description of the user that performed the latest action and
         * how many others have also performed this action
         * ex. mikechoch and 2 others
         * This is generic enough to handle following, likes, or reposts
         */
        private void constructNotificationTextViews() {
            String mostRecentUsername = userBasedNotification.getMostRecentPrismUser().getUsername();
            TextView usernameTextView = new TextView(context);
            usernameTextView.setText(mostRecentUsername);
            usernameTextView.setTextSize(16);
            usernameTextView.setTextColor(Color.WHITE);
            Typeface notificationTypeface = userBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
            usernameTextView.setTypeface(notificationTypeface);
            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, userBasedNotification.getMostRecentPrismUser());
                }
            });

            notificationDescriptionTextView.setText(userBasedNotification.getMostRecentPrismUser().getUsername());
        }

        /**
         * Using Glide populate the profile picture of the person who performed the notification action
         * Validates that the PrismUser has a profile picture attached
         * When profile picture ImageView is clicked intent to UserProfileActivity of PrismUser
         */
        private void populateProfilePic() {
            PrismUser mostRecentPrismUser = userBasedNotification.getMostRecentPrismUser();
            if (mostRecentPrismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(mostRecentPrismUser.getProfilePicture().getLowResProfilePicUri())
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int imageViewPadding = (int) (1 * Default.scale);
                                RoundedBitmapDrawable profilePictureDrawable =
                                        BitmapHelper.createCircularProfilePicture(
                                                context,
                                                userProfilePicImageView,
                                                mostRecentPrismUser.getProfilePicture().isDefault(),
                                                resource,
                                                imageViewPadding);
                                userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                            }
                        });

                userProfilePicImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToUserProfileActivity(context, userBasedNotification.getMostRecentPrismUser());
                    }
                });
            }
        }
    }

}
