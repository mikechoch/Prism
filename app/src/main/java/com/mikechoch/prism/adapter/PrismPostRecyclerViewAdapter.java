package com.mikechoch.prism.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.DisplayUserType;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;


public class PrismPostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int RECYCLER_VIEW_AD_THRESHOLD = 5;
    private final int PRISM_POST_VIEW_TYPE = 0;
    private final int GOOGLE_AD_VIEW_TYPE = 1;

    private Context context;
    public ArrayList<PrismPost> prismPostArrayList;


    public PrismPostRecyclerViewAdapter(Context context, ArrayList<PrismPost> prismPostArrayList) {
        this.context = context;
        this.prismPostArrayList = prismPostArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position % RECYCLER_VIEW_AD_THRESHOLD != 0) {
            return PRISM_POST_VIEW_TYPE;
        }
        return GOOGLE_AD_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case PRISM_POST_VIEW_TYPE:
                viewHolder = new PrismPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.prism_post_recycler_view_item_layout, parent, false));
                break;
            case GOOGLE_AD_VIEW_TYPE:
                viewHolder = new GoogleAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.prism_post_google_ad_recycler_view_item_layout, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0 || position % RECYCLER_VIEW_AD_THRESHOLD != 0) {
            int realPosition  = getRealPosition(position);
            ((PrismPostViewHolder) holder).setData(prismPostArrayList.get(realPosition));
        } else {
            ((GoogleAdViewHolder) holder).setData();
        }
    }

    @Override
    public int getItemCount() {
        return prismPostArrayList.size();
    }

    private int getRealPosition(int position) {
        if (RECYCLER_VIEW_AD_THRESHOLD == 0) {
            return position;
        } else {
            return position - position / RECYCLER_VIEW_AD_THRESHOLD;
        }
    }


    public class PrismPostViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout prismPostRelativeLayout;
        private ImageView userProfilePicImageView;
        private RelativeLayout postInformationRelativeLayout;
        private TextView prismUserTextView;
        private TextView prismPostDateTextView;
        private CardView prismPostImageCardView;
        private RelativeLayout prismPostImageRelativeLayout;
        private ImageView prismPostImageView;
        private ImageView likeHeartAnimationImageView;
        private TextView likesCountTextView;
        private ImageView likeButton;
        private ImageView repostIrisAnimationImageView;
        private TextView repostsCountTextView;
        private ImageView repostButton;
        private ImageView moreButton;
        private ProgressBar progressBar;

        private PrismPost prismPost;
        private String postId;
        private String postDate;
        private Integer likeCount;
        private Integer repostCount;
        private boolean isPostLiked;
        private boolean isPostReposted;


        PrismPostViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.prism_post_progress_bar);
            prismPostRelativeLayout = itemView.findViewById(R.id.under_maintenance_activity_relative_layout);
            userProfilePicImageView = itemView.findViewById(R.id.recycler_view_profile_pic_image_view);
            postInformationRelativeLayout = itemView.findViewById(R.id.recycler_view_post_info_relative_layout);
            prismUserTextView = itemView.findViewById(R.id.recycler_view_user_text_view);
            prismPostDateTextView = itemView.findViewById(R.id.recycler_view_date_text_view);
            prismPostImageCardView = itemView.findViewById(R.id.prism_post_image_card_view);
            prismPostImageRelativeLayout = itemView.findViewById(R.id.prism_post_image_relative_layout);
            prismPostImageView = itemView.findViewById(R.id.recycler_view_image_image_view);
            likeHeartAnimationImageView = itemView.findViewById(R.id.recycler_view_like_heart);
            repostIrisAnimationImageView = itemView.findViewById(R.id.recycler_view_repost_iris);

            // Image action button initializations
            likeButton = itemView.findViewById(R.id.image_like_button);
            repostButton = itemView.findViewById(R.id.image_repost_button);
            moreButton = itemView.findViewById(R.id.image_more_button);

            // Image like/repost count initializations
            likesCountTextView = itemView.findViewById(R.id.likes_count_text_view);
            repostsCountTextView = itemView.findViewById(R.id.shares_count_text_view);
        }

        /**
         * Set data for the PrismPostViewHolder interface elements
         */
        public void setData(PrismPost prismPostObject) {
            this.prismPost = prismPostObject;
            postId = this.prismPost.getPostId();
            postDate = Helper.getFancyDateDifferenceString(prismPost.getTimestamp() * -1);
            likeCount = this.prismPost.getLikes();
            repostCount = this.prismPost.getReposts();
            isPostLiked = CurrentUser.hasLiked(prismPost);
            isPostReposted = CurrentUser.hasReposted(prismPost);

            if (likeCount == null) likeCount = 0;
            if (repostCount == null) repostCount = 0;
            populateInterfaceElements();
        }

        /**
         * Populate fields related to the user who posted
         * Handles fields for user profile picture, username, and post date
         */
        private void setupPostUserUIElements() {
            if (prismPost.getPrismUser() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismPost.getPrismUser().getProfilePicture().getLowResProfilePicUri())
                        .apply(new RequestOptions().fitCenter())
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int imageViewPadding = (int) (1.5 * Default.scale);
                                RoundedBitmapDrawable profilePictureDrawable =
                                        BitmapHelper.createCircularProfilePicture(
                                                context,
                                                userProfilePicImageView,
                                                prismPost.getPrismUser().getProfilePicture().isDefault(),
                                                resource,
                                                imageViewPadding);
                                userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                            }
                        });
                prismUserTextView.setText(prismPost.getPrismUser().getUsername());
                prismPostDateTextView.setText(postDate);

                postInformationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToUserProfileActivity(context, prismPost.getPrismUser());
                    }
                });
            }
        }

        /**
         * Setup prismPostImageView
         * Sets the sizing, touch events, and Glide image loading
         */
        @SuppressLint("ClickableViewAccessibility")
        private void setupPostImageView() {
            // Show the ProgressBar while waiting for image to load
            progressBar.setVisibility(View.VISIBLE);

            /*
             * prismPostImageView will have a width of 90% of the screen
             * prismPostImageView will have a max height of 60% of the screen
             * This causes any images that are stronger in height to not span the entire screen
             */
//            prismPostImageView.getLayoutParams().width = (int) (Default.screenWidth * 0.9);
            prismPostImageView.setMaxHeight((int) (Default.screenHeight * 0.675));

            /*
             * Using the Glide library to populate the prismPostImageView
             * asBitmap: converts to Bitmap
             * thumbnail: previews a 5% loaded image while the rest of the image is being loaded
             * load: loads the prismPost URI
             * listener: confirms if the image was uploaded properly or not
             * into: the loaded image will be placed inside the prismPostImageView
             */
            Glide.with(context)
                    .asBitmap()
                    .load(prismPost.getImage())
                    .apply(new RequestOptions().fitCenter().override((int) (Default.screenWidth * 0.9), (int) (Default.screenHeight * 0.675)))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // TODO: @Mike we should hide progressBar here as well and display a toast or something
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            // TODO: @Mike we should hide progressBar here as well and display a toast or something
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            prismPostRelativeLayout.setBackground(new BitmapDrawable(
                                                    context.getResources(), BitmapHelper.blur(resource, 0.2f, 100)));
                                            prismPostImageCardView.animate()
                                                    .alpha(1f)
                                                    .setDuration(250)
                                                    .start();
                                            prismPostImageView.animate()
                                                    .alpha(1f)
                                                    .setDuration(250)
                                                    .start();
                                            progressBar.animate()
                                                    .alpha(0f)
                                                    .setDuration(0)
                                                    .withEndAction(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }).start();
                                        }
                                    });
                                }
                            }).start();
                            return false;
                        }
                    }).into(prismPostImageView);

            /*
             * GestureDetector used to replace the prismPostImageView TouchListener
             * This allows detection of Single, Long, and Double tap gestures
             */
            final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    IntentHelper.intentToPrismPostDetailActivity(context, prismPost);
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    handleLikeButtonClick();
                    return super.onDoubleTap(e);
                }
            });

            /*
             * Sets the TouchListener to be handled by the GestureDetector class
             * This allows detection of Single, Long, and Double tap gestures
             */
            prismPostImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    gestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }

        /**
         * Three action buttons are shown for each PrismPost
         * Like button likes the PrismPost
         * Repost button reposts the PrismPost to the users profile
         * More button offers a few options
         */
        private void setupActionButtons() {
            setupLikeActionButton();
            setupRepostActionButton();
            setupMoreActionButton();
        }

        /**
         * Setup more action Button
         * When pressed show a more AlertDialog with options being decided by PrismPost PrismUser
         */
        private void setupMoreActionButton() {
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InterfaceAction.startMoreActionButtonAnimation(moreButton);

                    boolean isCurrentUserThePostCreator = Helper.isPrismUserCurrentUser(prismPost.getPrismUser());
                    AlertDialog morePrismPostAlertDialog = InterfaceAction.createMorePrismPostAlertDialog(context, prismPost, isCurrentUserThePostCreator);
                    morePrismPostAlertDialog.show();
                }
            });
        }

        /**
         *
         */
        private void setupRepostActionButton() {
            InterfaceAction.setupRepostActionButton(context, repostButton, isPostReposted);

            String repostString = repostCount + Helper.getSingularOrPluralText(" repost", repostCount);
            repostsCountTextView.setText(repostString);

            repostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.isPrismUserCurrentUser(prismPost.getUid())) {
                        Helper.toast(context, Message.CANNOT_REPOST_OWN_POST);
                        return;
                    }
                    boolean performRepost = !CurrentUser.hasReposted(prismPost);
                    if (performRepost) {
                        repostIrisAnimationImageView.setVisibility(View.INVISIBLE);
                        AlertDialog repostConfirmationAlertDialog = InterfaceAction.createRepostConfirmationAlertDialog(context, prismPost, repostButton, repostsCountTextView);
                        repostConfirmationAlertDialog.show();
                    } else {
                        handleRepostButtonClick(false);
                    }
                }
            });

            repostsCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToDisplayUsersActivity(context, postId, DisplayUserType.REPOSTED_USERS);
                }
            });
        }

        /**
         *
         */
        private void setupLikeActionButton() {
            InterfaceAction.setupLikeActionButton(context, likeHeartAnimationImageView, likeButton, isPostLiked);

            String likeString = likeCount + Helper.getSingularOrPluralText(" like", likeCount);
            likesCountTextView.setText(likeString);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleLikeButtonClick();
                }
            });

            likesCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToDisplayUsersActivity(context, postId, DisplayUserType.LIKED_USERS);
;                }
            });
        }

        /**
         * Populate elements for current PrismPostViewHolder item
         */
        private void populateInterfaceElements() {
            prismUserTextView.setTypeface(Default.sourceSansProBold);
            prismPostDateTextView.setTypeface(Default.sourceSansProLight);
            likesCountTextView.setTypeface(Default.sourceSansProLight);
            repostsCountTextView.setTypeface(Default.sourceSansProLight);

            setupPostUserUIElements();
            setupPostImageView();
            setupActionButtons();
        }

        /**
         * Check liked_posts_map HashMap if it contains the postId or not. If it contains
         * the postId, then firebaseUser has already liked the post and perform UNLIKE operation
         * If it doesn't exist, firebaseUser has not liked it yet, and perform LIKE operation
         * Operation LIKE (performLIKE = true): does 3 things. First it adds the the firebaseUser's
         * uid to the LIKED_USERS table under the post. Then it adds the postId to the
         * USER_LIKES table under the firebaseUser. Then it adds the postId and timestamp to the
         * local liked_posts_map HashMap so that recycler view can update
         * Operation UNLIKE (performLike = false): undoes above 3 things
         * TODO: update comments
         */
        private void handleLikeButtonClick() {
            boolean performLike = !CurrentUser.hasLiked(prismPost);
            performUIActivitiesForLike(performLike);

            if (performLike) {
                DatabaseAction.performLike(prismPost);
            } else {
                DatabaseAction.performUnlike(prismPost);
            }
        }

        private void performUIActivitiesForLike(boolean performLike) {
            InterfaceAction.startLikeActionButtonAnimation(context, likeButton, performLike);
            InterfaceAction.startLikeActionAnimation(context, likeHeartAnimationImageView, performLike);

            likeCount = performLike ? likeCount + 1 : likeCount - 1;
            prismPost.setLikes(likeCount);
            String likeString = likeCount + Helper.getSingularOrPluralText(" like", likeCount);
            likesCountTextView.setText(likeString);
        }


        /**
         * Check reposted_posts_map HashMap if it contains the postId or not. If it contains
         * the postId, then firebaseUser has already reposted the post and perform UNREPOST operation
         * If it doesn't exist, firebaseUser has not reposted it yet, and perform REPOST operation
         * Operation REPOST (performRepost = true): does 3 things. First it adds the the firebaseUser's
         * uid to the REPOSTED_USERS table under the post. Then it adds the postId to the
         * USER_REPOSTS table under the firebaseUser. Then it adds the postId and timestamp to the
         * local reposted_posts_map HashMap so that recycler view can update
         * Operation UNREPOST (performRepost = false): undoes above 3 things
         * TODO: update comments
         * @param performRepost -
         */
        private void handleRepostButtonClick(boolean performRepost) {
            performUIActivitiesForRepost(performRepost);

            if (performRepost) {
                DatabaseAction.performRepost(prismPost);
            } else {
                DatabaseAction.performUnrepost(prismPost);
            }
        }

        private void performUIActivitiesForRepost(boolean performRepost) {
            InterfaceAction.startRepostActionButtonAnimation(context, repostButton, performRepost);

            repostCount = prismPost.getReposts() + (performRepost ? 1 : -1);
            prismPost.setReposts(repostCount);
            String repostString = repostCount + Helper.getSingularOrPluralText(" repost", repostCount);
            repostsCountTextView.setText(repostString);
        }

    }


    public class GoogleAdViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout sponsoredLinearLayout;
        private TextView sponsoredAdTextView;
        private AdView adView;
        private ProgressBar adProgressBar;
        private LinearLayout adFailedLinearLayout;
        private TextView adFailedTextView;
        private AdRequest adRequest;


        GoogleAdViewHolder(View itemView) {
            super(itemView);

            sponsoredLinearLayout = itemView.findViewById(R.id.prism_post_google_ad_sponsored_linear_layout);
            sponsoredAdTextView = itemView.findViewById(R.id.prism_post_sponsored_ad_text_view);
            adView = itemView.findViewById(R.id.prism_post_google_ad_view);
            adProgressBar = itemView.findViewById(R.id.prism_post_google_ad_item_progress_bar);
            adFailedLinearLayout = itemView.findViewById(R.id.prism_post_google_ad_item_failed_ad_layout);
            adFailedTextView = itemView.findViewById(R.id.prism_post_google_ad_item_failed_ad_layout_title);
        }

        /**
         * Set data method for GoogleAdViewHolder
         * Usually for normal ViewHolders we expect an object as a param, but in this case
         * we are just showing an ad
         */
        private void setData() {
            populateInterfaceElements();
        }

        /**
         * Populate all interface elements for GoogleAdViewHolder
         */
        private void populateInterfaceElements() {
            sponsoredAdTextView.setTypeface(Default.sourceSansProBold);
            adFailedTextView.setTypeface(Default.sourceSansProBold);

            adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    sponsoredLinearLayout.setVisibility(View.VISIBLE);
                    adView.setVisibility(View.VISIBLE);
                    adProgressBar.setVisibility(View.GONE);
                    adFailedLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    sponsoredLinearLayout.setVisibility(View.GONE);
                    adView.setVisibility(View.GONE);
                    adProgressBar.setVisibility(View.GONE);
                    adFailedLinearLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }
    }
}
