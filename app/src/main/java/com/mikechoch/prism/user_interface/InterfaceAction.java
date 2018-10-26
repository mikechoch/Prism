package com.mikechoch.prism.user_interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.OptionRecyclerViewAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.action.OnDeletePostCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fragment.MainFeedFragment;
import com.mikechoch.prism.helper.AnimationBounceInterpolator;
import com.mikechoch.prism.type.MoreOption;


public class InterfaceAction {

    public static int[] swipeRefreshLayoutColors = {R.color.colorAccent};

    private static Animation likeHeartBounceAnimation;
    private static Animation repostIrisBounceAnimation;
    private static Animation unrepostIrisBounceAnimation;
    private static Animation likeButtonBounceAnimation;
    private static Animation shareButtonBounceAnimation;
    private static Animation moreButtonBounceAnimation;


    public InterfaceAction(Context context) {

        // Load all animations from anim folder for action buttons
        likeHeartBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.like_animation);
        repostIrisBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.repost_animation);
        unrepostIrisBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.unrepost_animation);
        likeButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);
        shareButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);
        moreButtonBounceAnimation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_animation);

        // Use bounce interpolator with amplitude 0.2 and frequency 20, gives bounce affect on buttons
        AnimationBounceInterpolator buttonAnimationBounceInterpolator = new AnimationBounceInterpolator(0.2, 20);
        likeButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        shareButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        moreButtonBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
        likeHeartBounceAnimation.setInterpolator(buttonAnimationBounceInterpolator);
    }

    /**
     * Toggle keyboard method, true shows keyboard
     * @param context - context of current method
     * @param view - main view of current activity to hide or show keyboard in
     * @param showKeyboard - boolean for showing or hiding keyboard
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
     * @param context - context of current method
     * @param performLike - boolean if like was performed to create correct Drawable
     * @return - solid blue heart if performLike is true
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
     * Create the like Drawable and start the animation for the heart like button
     * @param context - context of current method
     * @param likeActionButton - heart ImageView button to be animated
     * @param performLike - boolean performLike handles Drawable creation
     */
    public static void startLikeActionButtonAnimation(Context context, ImageView likeActionButton, boolean performLike) {
        Drawable buttonDrawable = createLikeDrawable(context, performLike);
        likeActionButton.setImageDrawable(buttonDrawable);
        likeActionButton.startAnimation(likeButtonBounceAnimation);
    }

    /**
     * Using the correct Drawable animate a full heart for liking an image over the ImageView
     * @param context - context of current method
     * @param likeActionImageView - the heart ImageView
     * @param performLike - boolean performLike for showing correct Drawable
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
     * Pass in a boolean that toggles the color of the like button
     * @param context - context of current method
     * @param likeActionImageView - heart ImageView to set correct Drawable in
     * @param likeActionButton - heart ImageView button to animate and change Drawable for
     * @param isPostLiked - isPostLiked determines correct Drawables to populate ImageViews
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
     * @param context - context of current method
     * @param performRepost - performRepost boolean for creating correct Drawable
     * @return - Drawable for repost ImageView
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
     * Handle Drawable and animation of repost Button and ImageView
     * @param context - context of current method
     * @param repostActionButton - repost Button to toggle Drawable and animate
     * @param performRepost - performRepost boolean for creating Drawable
     */
    public static void startRepostActionButtonAnimation(Context context, ImageView repostActionButton, boolean performRepost) {
        Drawable drawable = createRepostDrawable(context, performRepost);
        repostActionButton.setImageDrawable(drawable);
        repostActionButton.startAnimation(shareButtonBounceAnimation);
    }

    /**
     * Create the repost Drawable and start the animation for the iris repost button
     * @param repostActionImageView - repost ImageView to animate
     * @param performRepost - performRepost boolean for correct animation
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
     * Setup the repost ImageView button Drawable
     * @param context - context of current method
     * @param repostActionButton - repost ImageView button to change Drawable of
     * @param isPostReposted - isPostReposted boolean to get correct repost Drawable
     */
    public static void setupRepostActionButton(Context context, ImageView repostActionButton, boolean isPostReposted) {
        Drawable repostDrawable = createRepostDrawable(context, isPostReposted);
        repostActionButton.setImageDrawable(repostDrawable);
    }

    /**
     * Animate the repost ImageView button
     * @param moreActionButton - repost ImageView button to animate
     */
    public static void startMoreActionButtonAnimation(ImageView moreActionButton) {
        moreActionButton.startAnimation(moreButtonBounceAnimation);
    }

    /**
     * AlertDialog to confirm the repost of a post
     * @param context - context of current method
     * @param prismPost - PrismPost to show repost confirmation alert dialog for
     * @param repostActionButton - repost ImageView button
     * @param repostCountTextView - repost count TextView to change text of
     * @return - AlertDialog for confirming the reposting of a PrismPost
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
     * @param context - context of current method
     * @param prismPost - PrismPost to show more alert dialog for
     * @param isCurrentUser - boolean representing if current user or not
     * @return - finalized AlertDialog for more button click
     */
    public static AlertDialog createMorePrismPostAlertDialog(Context context, PrismPost prismPost, boolean isCurrentUser) {
        RecyclerView recyclerView = new RecyclerView(context);

        AlertDialog.Builder moreOptionAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        moreOptionAlertDialogBuilder.setView(recyclerView);
        AlertDialog moreOptionAlertDialog = moreOptionAlertDialogBuilder.create();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        OptionRecyclerViewAdapter moreOptionsRecyclerViewAdapter = new OptionRecyclerViewAdapter(context, MoreOption.values(), prismPost, isCurrentUser, moreOptionAlertDialog);
        recyclerView.setAdapter(moreOptionsRecyclerViewAdapter);

        return moreOptionAlertDialog;
    }

    /**
     * AlertDialog to confirm the reporting of a post
     * @param context - context of current method
     * @param prismPost - PrismPost to create AlertDialog for
     * @return - finalized AlertDialog for reposting a post
     */
    public static AlertDialog createReportPostConfirmationAlertDialog(Context context, PrismPost prismPost) {
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
     * @param context - context of current method
     * @param prismPost - PrismPost to create AlertDialog for
     * @return - finalized AlertDialog for deleting a post
     */
    public static AlertDialog createDeleteConfirmationAlertDialog(Context context, PrismPost prismPost) {
        AlertDialog.Builder deleteAlertDialogBuilder = new AlertDialog.Builder(context, R.style.DarkThemAlertDialog);
        deleteAlertDialogBuilder.setTitle("Are you sure you want to delete this post?");
        deleteAlertDialogBuilder.setPositiveButton(Default.BUTTON_DELETE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                DatabaseAction.deletePost(prismPost, new OnDeletePostCallback() {
                    @Override
                    public void onSuccess() {
                        dialogInterface.cancel();
                        // TODO Display a toast?
                        // Update UI after the post is deleted
                        MainFeedFragment.mainFeedPrismPostArrayList.remove(prismPost);
                        //TODO: We need to call notify data set changed here

                    }

                    @Override
                    public void onPostNotFound() {
                        // TODO Log this and display a toast
                    }

                    @Override
                    public void onPermissionDenied() {
                        // TODO Log this and display a toast
                    }

                    @Override
                    public void onImageDeleteFail(Exception e) {
                        dialogInterface.cancel();
                        // TODO Log this and display a toast
                    }

                    @Override
                    public void onPostDeleteFail(Exception e) {
                        dialogInterface.cancel();
                        // TODO Log this and display a toast
                    }
                });

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
     * @param context - context of current method
     * @param prismUser - PrismUser to create AlertDialog for
     * @param toolbarFollowButton - toolbar follow Button to modify
     * @param followUserButton - follow user Button to modify
     * @return - finalized AlertDialog for unfollowing a PrismUser
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
     * Toggle method for interface of small follow button
     * @param context - context of current method
     * @param showFollowing - boolean to determine what following text to show
     * @param smallFollowButton - passed in small follow button
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
     * Toggle method for interface of large follow button
     * @param context - context of current method
     * @param showFollowing - boolean to determine what following text to show
     * @param largeFollowButton - passed in large follow button
     */
    public static void toggleLargeFollowButton(Context context, boolean showFollowing, Button largeFollowButton) {
        String followButtonString = showFollowing ? "Following" : "Follow";
        int followButtonInt = showFollowing ? R.drawable.button_selector_selected : R.drawable.button_selector;
        Drawable followingButtonDrawable = context.getResources().getDrawable(followButtonInt);

        largeFollowButton.setText(followButtonString);
        largeFollowButton.setBackground(followingButtonDrawable);
    }

    /**
     * Handle the follow button when clicked to update firebase
     * @param context - context of current method
     * @param performFollow - boolean to determine what follow action to take
     * @param userFollowButton - passed in follow button
     * @param prismUser - the PrismUser to perform follow on
     */
    public static void handleFollowButtonClick(Context context, boolean performFollow, Button userFollowButton, PrismUser prismUser) {
        if (performFollow) {
            InterfaceAction.toggleSmallFollowButton(context, true, userFollowButton);
            DatabaseAction.followUser(prismUser);
        } else {
            AlertDialog unfollowAlertDialog = InterfaceAction.createUnfollowConfirmationAlertDialog(context, prismUser, userFollowButton, null);
            unfollowAlertDialog.show();
        }
    }

}
