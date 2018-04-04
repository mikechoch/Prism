package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constants.Default;
import com.mikechoch.prism.constants.Key;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.NotificationType;

import java.util.ArrayList;

/**
 * Created by mikechoch on 1/21/18.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    /*
     * Global variables
     */
    private Context context;
    public static ArrayList<Notification> notificationArrayList;

    private float scale;
    private Typeface sourceSansProLight;
    private Typeface sourceSansProBold;
    private int screenWidth;
    private int screenHeight;


    public NotificationRecyclerViewAdapter(Context context, ArrayList<Notification> notificationArrayList, int[] screenDimens) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
        this.screenWidth = screenDimens[0];
        this.screenHeight = screenDimens[1];

        this.scale = context.getResources().getDisplayMetrics().density;
        this.sourceSansProLight = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Light.ttf");
        this.sourceSansProBold = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Black.ttf");
        this.screenWidth = screenDimens[0];
        this.screenHeight = screenDimens[1];
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(notificationArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseAuth auth;
        private DatabaseReference postAuthorUserReference;
        private StorageReference storageReference;
        private DatabaseReference allPostsReference;

        private ImageView userProfilePicImageView;
        private TextView notificationDescriptionTextView;
        private ImageView prismPostThumbnailImageView;

        private Notification notification;


        public ViewHolder(View itemView) {
            super(itemView);

            // Cloud database initializations
            auth = FirebaseAuth.getInstance();
            postAuthorUserReference = Default.USERS_REFERENCE.child(auth.getCurrentUser().getUid());
            storageReference = Default.STORAGE_REFERENCE.child(Key.STORAGE_POST_IMAGES_REF);
            allPostsReference = Default.ALL_POSTS_REFERENCE;

            userProfilePicImageView = itemView.findViewById(R.id.notification_item_prism_profile_image_view);
            notificationDescriptionTextView = itemView.findViewById(R.id.notification_item_description_text_view);
            prismPostThumbnailImageView = itemView.findViewById(R.id.notification_item_post_thumbnail_image_view);
        }

        /**
         * Set data for the ViewHolder UI elements
         */
        public void setData(Notification notificationObject) {
            this.notification = notificationObject;
            populateUIElements();
        }

        /**
         *
         */
        private void populateUIElements() {
            String notificationMessage = Helper.constructNotificationMessage(notification);
            notificationDescriptionTextView.setText(notificationMessage);
            notificationDescriptionTextView.setTypeface(sourceSansProLight);
            populateProfilePic();

            if (!notification.getType().equals(NotificationType.FOLLOW)) {
                populatePrismPostThumbnail();
            }

        }

        private void populatePrismPostThumbnail() {
            PrismPost notificationPost = notification.getPrismPost();
            if (notificationPost != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(notificationPost.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(prismPostThumbnailImageView);
            }
        }

        private void populateProfilePic() {
            PrismUser mostRecentPrismUser = notification.getMostRecentUser();
            if (mostRecentPrismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(mostRecentPrismUser.getProfilePicture().lowResUri)
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                if (!mostRecentPrismUser.getProfilePicture().isDefault) {
                                    int whiteOutlinePadding = (int) (1 * scale);
                                    userProfilePicImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                    userProfilePicImageView.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_frame));
                                } else {
                                    userProfilePicImageView.setPadding(0, 0, 0, 0);
                                    userProfilePicImageView.setBackground(null);
                                }

                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                drawable.setCircular(true);
                                userProfilePicImageView.setImageDrawable(drawable);
                            }
                        });
            }
        }
    }


}
