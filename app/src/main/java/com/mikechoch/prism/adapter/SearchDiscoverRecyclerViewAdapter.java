package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.DiscoveryPost;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.Discovery;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;

public class SearchDiscoverRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PRISM_POST_VIEW_TYPE = 0;
    private final int PRISM_USER_VIEW_TYPE = 1;

    private Context context;
    private ArrayList<?> prismDataArrayList;
    private Discovery discoveryType;


    public SearchDiscoverRecyclerViewAdapter(Context context, ArrayList<?> prismDataArrayList, Discovery discoveryType) {
        this.context = context;
        this.prismDataArrayList = prismDataArrayList;
        this.discoveryType = discoveryType;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = PRISM_POST_VIEW_TYPE;
        Object data = prismDataArrayList.get(position);
        if (data instanceof PrismUser) {
            viewType = PRISM_USER_VIEW_TYPE;
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = new PrismPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.discover_prism_post_recycler_view_item_layout, parent, false));
        switch (viewType) {
            case PRISM_USER_VIEW_TYPE:
                viewHolder = new PrismUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.discover_prism_user_recycler_view_item_layout, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object data = prismDataArrayList.get(position);
        if (data instanceof PrismPost){
            ((PrismPostViewHolder) holder).setData((PrismPost) data);
        } else if (data instanceof PrismUser) {
            ((PrismUserViewHolder) holder).setData((PrismUser) data);
        }
    }

    @Override
    public int getItemCount() {
        return prismDataArrayList.size();
    }


    public class PrismPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView prismPostImageView;
        private TextView prismPostUsername;
        private TextView prismPostCount;
        private ImageView prismPostUserProfilePicture;


        private PrismPostViewHolder(View itemView) {
            super(itemView);

            prismPostImageView = itemView.findViewById(R.id.discover_prism_post_image_view);
            prismPostUsername = itemView.findViewById(R.id.discover_prism_post_user_text_view);
            prismPostCount = itemView.findViewById(R.id.discover_prism_post_date_count_text_view);
            prismPostUserProfilePicture = itemView.findViewById(R.id.discover_prism_post_profile_picture_image_view);

            populateInterfaceElements();
        }

        /**
         * Set data for the PrismPostViewHolder interface elements
         */
        public void setData(PrismPost prismPost) {
            if (prismPost.getPrismUser() != null) {
                String username = prismPost.getPrismUser().getUsername();
                prismPostUsername.setText(username);
                prismPostUsername.setSelected(true);

                String fancyDate = Helper.getFancyDateDifferenceString(-1 * prismPost.getTimestamp());
                String countString = "";
                switch (discoveryType) {
                    case LIKE:
                        countString += fancyDate + " • " + prismPost.getLikes() + " likes";
                        prismPostCount.setText(countString);
                        break;
                    case REPOST:
                        countString += fancyDate + " • " + prismPost.getReposts() + " reposts";
                        prismPostCount.setText(countString);
                        break;
                    case TAG:
                        prismPostCount.setText(fancyDate);
                        break;
                }
                prismPostCount.setSelected(true);

                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismPost.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(prismPostImageView);

                prismPostImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToPrismPostDetailActivity(context, prismPost);
                    }
                });

                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismPost.getPrismUser().getProfilePicture().lowResUri)
                        .apply(new RequestOptions().fitCenter())
                        .into(new BitmapImageViewTarget(prismPostUserProfilePicture) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                if (!prismPost.getPrismUser().getProfilePicture().isDefault) {
                                    int whiteOutlinePadding = (int) (1.5 * Default.scale);
                                    prismPostUserProfilePicture.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                    prismPostUserProfilePicture.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                                } else {
                                    prismPostUserProfilePicture.setPadding(0, 0, 0, 0);
                                    prismPostUserProfilePicture.setBackground(null);
                                }

                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                drawable.setCircular(true);
                                prismPostUserProfilePicture.setImageDrawable(drawable);
                            }
                        });

                prismPostUserProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToUserProfileActivity(context, prismPost.getPrismUser());
                    }
                });
            }

        }

        /**
         * Populate elements in the current PrismPostViewHolder
         */
        private void populateInterfaceElements() {
            // Setup Typefaces for all text based UI elements
            prismPostUsername.setTypeface(Default.sourceSansProBold);
            prismPostCount.setTypeface(Default.sourceSansProLight);

        }
    }


    public class PrismUserViewHolder extends RecyclerView.ViewHolder {

        private CardView discoverPrismUserCardView;
        private ImageView discoverPrismUserImageView;
        private Button discoverPrismUserFollowButton;
        private TextView discoverPrismUserUsername;
        private TextView discoverPrismUserName;

        private PrismUser prismUser;

        private PrismUserViewHolder(View itemView) {
            super(itemView);

            discoverPrismUserCardView = itemView.findViewById(R.id.discover_prism_user_card_view);
            discoverPrismUserImageView = itemView.findViewById(R.id.discover_prism_user_profile_picture_image_view);
            discoverPrismUserFollowButton = itemView.findViewById(R.id.discover_prism_user_follow_user_button);
            discoverPrismUserUsername = itemView.findViewById(R.id.discover_prism_user_username_text_view);
            discoverPrismUserName = itemView.findViewById(R.id.discover_prism_user_name_text_view);

            populateInterfaceElements();
        }

        /**
         * Set data for the current PrismUserViewHolder elements
         */
        public void setData(PrismUser prismUser) {
            this.prismUser = prismUser;
            populateInterfaceElements();
        }

        /**
         * Populate elements in the current PrismUserViewHolder
         */
        private void populateInterfaceElements() {
            discoverPrismUserUsername.setTypeface(Default.sourceSansProBold);
            discoverPrismUserName.setTypeface(Default.sourceSansProLight);

            setupPrismUserView();
        }

        /**
         * Setup PrismUser view for current PrismUserViewHolder
         */
        private void setupPrismUserView() {
            if (prismUser != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismUser.getProfilePicture().lowResUri)
                        .apply(new RequestOptions().fitCenter())
                        .into(new BitmapImageViewTarget(discoverPrismUserImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                if (!prismUser.getProfilePicture().isDefault) {
                                    int whiteOutlinePadding = (int) (1.5 * Default.scale);
                                    discoverPrismUserImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                    discoverPrismUserImageView.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                                } else {
                                    discoverPrismUserImageView.setPadding(0, 0, 0, 0);
                                    discoverPrismUserImageView.setBackground(null);
                                }

                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                drawable.setCircular(true);
                                discoverPrismUserImageView.setImageDrawable(drawable);
                            }
                        });

                // TODO: this check here should not be necessary since I won't be giving you currentUser in the ArrayList, right?
                if (!Helper.isPrismUserCurrentUser(prismUser)) {
                    discoverPrismUserFollowButton.setVisibility(View.VISIBLE);
                    InterfaceAction.toggleSmallFollowButton(context, CurrentUser.isFollowingPrismUser(prismUser), discoverPrismUserFollowButton);
                    discoverPrismUserFollowButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean performFollow = !CurrentUser.isFollowingPrismUser(prismUser);
                            InterfaceAction.handleFollowButtonClick(context, performFollow, discoverPrismUserFollowButton, prismUser);
                        }
                    });

                    discoverPrismUserFollowButton.setTypeface(Default.sourceSansProLight);
                }

                discoverPrismUserUsername.setText(prismUser.getUsername());
                discoverPrismUserName.setText(prismUser.getFullName());

                discoverPrismUserUsername.setSelected(true);
                discoverPrismUserName.setSelected(true);
            }
        }
    }

}
