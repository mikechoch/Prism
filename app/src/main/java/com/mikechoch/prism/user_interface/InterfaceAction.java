package com.mikechoch.prism.user_interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.helper.AnimationBounceInterpolator;

/**
 * Created by mikechoch on 2/26/18.
 */

public class InterfaceAction {

    /*
     * Globals
     */
    public static String[] setProfilePicStrings = {"Choose from gallery", "Take a selfie", "View profile picture"};
    public static int[] swipeRefreshLayoutColors = {R.color.colorAccent};

    private Context context;

    private static String[] morePostOptionsCurrentUser = {"Report post", "Share", "Delete"};
    private static String[] morePostOptions = {"Report post", "Share"};

    // Use bounce interpolator with amplitude 0.2 and frequency 20, gives bounce affect on buttons
    private AnimationBounceInterpolator buttonAnimationBounceInterpolator = new AnimationBounceInterpolator(0.2, 20);

    // Action Button Animations
    private static Animation likeHeartBounceAnimation;
    private static Animation repostIrisBounceAnimation;
    private static Animation unrepostIrisBounceAnimation;
    private static Animation likeButtonBounceAnimation;
    private static Animation shareButtonBounceAnimation;
    private static Animation moreButtonBounceAnimation;


    public InterfaceAction(Context context) {
        this.context = context;

        // Load all animations from anim folder for action buttons
        this.likeHeartBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.like_animation);
        this.repostIrisBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.repost_animation);
        this.unrepostIrisBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.unrepost_animation);
        this.likeButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);
        this.shareButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);
        this.moreButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);

        //
        this.likeButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        this.shareButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        this.moreButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        this.likeHeartBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
    }

    /**
     *
     * @param context
     * @param view
     */
    public static void toggleKeyboard(Context context, View view, boolean showKeyboard) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            if (showKeyboard) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * Pass in a boolean that toggles the icon and color of the like button
     */
    private static Drawable createLikeDrawable(Context context, boolean performLike) {
        int heart = performLike ? R.drawable.ic_heart_white_36dp : R.drawable.ic_heart_outline_black_36dp;
        int color = performLike ? R.color.colorAccent : android.R.color.white;
        Drawable heartDrawable = context.getResources().getDrawable(heart);
        int heartColor = context.getResources().getColor(color);
        heartDrawable.setTint(heartColor);
        return heartDrawable;
    }

    /**
     *
     */
    public static void startLikeActionButtonAnimation(Context context, ImageView likeActionButton, boolean performLike) {
        Drawable buttonDrawable = createLikeDrawable(context, performLike);
        likeActionButton.setImageDrawable(buttonDrawable);
        likeActionButton.startAnimation(likeButtonBounceAnimation);
    }

    /**
     *
     */
    public static void startLikeActionAnimation(Context context, ImageView likeActionImageView, boolean performLike) {
        Drawable drawable = context.getResources().getDrawable(
                performLike ? R.drawable.like_heart : R.drawable.unlike_heart);
        likeActionImageView.setImageDrawable(drawable);
        likeHeartBounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                likeActionImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                likeActionImageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        likeActionImageView.startAnimation(likeHeartBounceAnimation);
    }

    /**
     *
     */
    public static void setupLikeActionButton(Context context, ImageView likeActionImageView, ImageView likeActionButton, boolean isPostLiked) {
        Drawable heartButtonDrawable = createLikeDrawable(context, isPostLiked);
        likeActionButton.setImageDrawable(heartButtonDrawable);
        Drawable heartDrawable = context.getResources().getDrawable(
                isPostLiked ?  R.drawable.unlike_heart : R.drawable.like_heart);

        if (likeActionImageView != null) {
            likeActionImageView.setImageDrawable(heartDrawable);
        }
    }

    /**
     * Pass in a boolean that toggles the color of the repost button
     */
    private static Drawable createRepostDrawable(Context context, boolean performRepost) {
        int repost = R.drawable.ic_camera_iris_black_36dp;
        Drawable repostDrawable = context.getResources().getDrawable(repost);
        int color = performRepost ? R.color.colorAccent : android.R.color.white;
        int repostColor = context.getResources().getColor(color);
        repostDrawable.setTint(repostColor);
        return repostDrawable;
    }

    /**
     *
     */
    public static void startRepostActionButtonAnimation(Context context, ImageView repostActionButton, boolean performRepost) {
        Drawable drawable = createRepostDrawable(context, performRepost);
        repostActionButton.setImageDrawable(drawable);
        repostActionButton.startAnimation(shareButtonBounceAnimation);
    }

    /**
     *
     */
    public static void startRepostActionAnimation(ImageView repostActionImageView, boolean performRepost) {
        repostIrisBounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                repostActionImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                repostActionImageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        repostActionImageView.startAnimation(performRepost ? repostIrisBounceAnimation : unrepostIrisBounceAnimation);
    }

    /**
     *
     */
    public static void setupRepostActionButton(Context context, ImageView repostActionButton, boolean isPostReposted) {
        Drawable repostDrawable = createRepostDrawable(context, isPostReposted);
        repostActionButton.setImageDrawable(repostDrawable);
    }

    /**
     *
     */
    public static void startMoreActionButtonAnimation(ImageView moreActionButton) {
        moreActionButton.startAnimation(moreButtonBounceAnimation);
    }

    /**
     * AlertDialog to confirm the repost of a post
     */
    public static AlertDialog createRepostConfirmationAlertDialog(Context context, PrismPost prismPost, ImageView repostActionButton, TextView repostCountTextView) {
        AlertDialog.Builder repostConfirmationAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        repostConfirmationAlertDialogBuilder.setTitle("This post will be shown on your profile, do you want to repost?");
        repostConfirmationAlertDialogBuilder
                .setPositiveButton(Default.BUTTON_REPOST, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        startRepostActionButtonAnimation(context, repostActionButton, true);

                        String repostsText = repostCountTextView.getText().toString();
                        String[] repostsTextArray = repostsText.split(" ");
                        int repostsCount = Integer.parseInt(repostsTextArray[0]) + 1;
                        repostsTextArray[0] = String.valueOf(repostsCount);
                        repostsText = TextUtils.join(" ", repostsTextArray);
                        repostCountTextView.setText(repostsText);
                        prismPost.setReposts(repostsCount);

                        DatabaseAction.performRepost(prismPost);
                    }
                }).setNegativeButton(Default.BUTTON_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
        });
        return repostConfirmationAlertDialogBuilder.create();
    }

    /**
     * AlertDialog that shows several options to the user when the moreActionButton is clicked
     * Report and SHare will show for all users
     * Delete will only show for user who posted the post
     * @param context
     * @param prismPost
     * @param isCurrentUser
     * @return finalized AlertDialog for more button click
     */
    public static AlertDialog createMorePrismPostAlertDialog(Context context, PrismPost prismPost, boolean isCurrentUser) {
        AlertDialog.Builder profilePictureAlertDialog = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        profilePictureAlertDialog.setItems(isCurrentUser ? morePostOptionsCurrentUser : morePostOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        // Report post
                        AlertDialog reportPostConfirmationAlertDialog = createReportPostConfirmationAlertDialog(context, prismPost);
                        reportPostConfirmationAlertDialog.show();
                        break;
                    case 1:
                        // Share

                        break;
                    case 2:
                        // Delete
                        AlertDialog deleteConfirmationAlertDialog = createDeleteConfirmationAlertDialog(context, prismPost);
                        deleteConfirmationAlertDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
        return profilePictureAlertDialog.create();
    }

    private static AlertDialog createReportPostConfirmationAlertDialog(Context context, PrismPost prismPost) {
        AlertDialog.Builder reportAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        reportAlertDialogBuilder.setTitle("Are you sure you want to report this post as inappropriate?");
        reportAlertDialogBuilder.setMessage("We will review this post and take the appropriate action");
        reportAlertDialogBuilder.setPositiveButton(Default.BUTTON_REPORT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                DatabaseAction.reportPost(context, prismPost);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return reportAlertDialogBuilder.create();
    }

    /**
     * AlertDialog to confirm the deletion of a post
     * @param context
     * @param prismPost
     * @return finalized AlertDialog for deleting a post
     */
    private static AlertDialog createDeleteConfirmationAlertDialog(Context context, PrismPost prismPost) {
        AlertDialog.Builder deleteAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        deleteAlertDialogBuilder.setTitle("Are you sure you want to delete this post?");
        deleteAlertDialogBuilder.setPositiveButton(Default.BUTTON_DELETE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                DatabaseAction.deletePost(prismPost);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return deleteAlertDialogBuilder.create();
    }

    /**
     * AlertDialog to confirm the unfollowing a PrismUser
     * @param context
     * @param prismUser
     * @return finalized AlertDialog for unfollowing a PrismUser
     */
    public static AlertDialog createUnfollowConfirmationAlertDialog(Context context, PrismUser prismUser, Button toolbarFollowButton, Button followUserButton) {
        AlertDialog.Builder unfollowAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        unfollowAlertDialogBuilder.setTitle("Are you sure you want to unfollow this user?");
        unfollowAlertDialogBuilder.setPositiveButton(Default.BUTTON_UNFOLLOW, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (toolbarFollowButton != null) {
                    InterfaceAction.toggleSmallFollowButton(context, false, toolbarFollowButton);
                }
                if (followUserButton != null) {
                    InterfaceAction.toggleLargeFollowButton(context, false, followUserButton);
                }
                DatabaseAction.unfollowUser(prismUser);
            }
        }).setNegativeButton(Default.BUTTON_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return unfollowAlertDialogBuilder.create();
    }

    /**
     * Toggle method for UI of small follow button
     */
    public static void toggleSmallFollowButton(Context context, boolean showFollowing, Button smallFollowButton) {
        int buttonWidth = (int) (Default.scale * (showFollowing ? 80 : 60));
        String followButtonString = showFollowing ? "Following" : "Follow";
        int followButtonInt = showFollowing ? R.drawable.button_selector_selected : R.drawable.button_selector;
        Drawable followingToolbarButtonDrawable = context.getResources().getDrawable(followButtonInt);

        smallFollowButton.getLayoutParams().width = buttonWidth;
        smallFollowButton.setText(followButtonString);
        smallFollowButton.setBackground(followingToolbarButtonDrawable);
        smallFollowButton.requestLayout();
    }

    /**
     * Toggle method for UI of large follow button
     */
    public static void toggleLargeFollowButton(Context context, boolean showFollowing, Button largeFollowButton) {
        String followButtonString = showFollowing ? "Following" : "Follow";
        int followButtonInt = showFollowing ? R.drawable.button_selector_selected : R.drawable.button_selector;
        Drawable followingButtonDrawable = context.getResources().getDrawable(followButtonInt);

        largeFollowButton.setText(followButtonString);
        largeFollowButton.setBackground(followingButtonDrawable);
    }

}
