package com.mikechoch.prism.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.DiscoveryPost;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Discovery;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;
import java.util.Random;

public class SearchDiscoverRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int RECYCLER_VIEW_AD_THRESHOLD = 7;
    private final int PRISM_POST_VIEW_TYPE = 0;
    private final int PRISM_USER_VIEW_TYPE = 1;
    private final int GOOGLE_AD_VIEW_TYPE = 2;

    private Context context;
    private ArrayList<?> prismDataArrayList;
    private Discovery discoveryType;
    private Random random;


    public SearchDiscoverRecyclerViewAdapter(Context context, ArrayList<?> prismDataArrayList, Discovery discoveryType) {
        this.context = context;
        this.prismDataArrayList = prismDataArrayList;
        this.discoveryType = discoveryType;
        random = new Random();
        random.setSeed(Long.MAX_VALUE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % RECYCLER_VIEW_AD_THRESHOLD != RECYCLER_VIEW_AD_THRESHOLD - 1) {
            int realPosition  = getRealPosition(position);
            Object data = prismDataArrayList.get(realPosition);
            if (data instanceof PrismPost){
                return PRISM_POST_VIEW_TYPE;
            } else if (data instanceof PrismUser) {
                return PRISM_USER_VIEW_TYPE;
            } /* else if (data instanceof DiscoveryPost) {
                return DISCOVERY_POST_VIEW_TYPE; // TODO create this
            } */
        }
        return GOOGLE_AD_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case PRISM_POST_VIEW_TYPE:
                viewHolder = new PrismPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.discover_prism_post_recycler_view_item_layout, parent, false));
                break;
            case PRISM_USER_VIEW_TYPE:
                viewHolder = new PrismUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.discover_prism_user_recycler_view_item_layout, parent, false));
                break;
            case GOOGLE_AD_VIEW_TYPE:
                viewHolder = new GoogleAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.discover_prism_post_google_ad_recycler_view_item_layout, parent, false));
                break;
             // TODO @MIKE create a new view for DiscoveryPost
            /* case DISCOVERY_POST_VIEW_TYPE:
                viewHolder = new DiscoveryPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.discover_discovery_post_recycler_view_item_layout, parent, false)); */
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position % RECYCLER_VIEW_AD_THRESHOLD != RECYCLER_VIEW_AD_THRESHOLD - 1) {
            int realPosition  = getRealPosition(position);
            Object data = prismDataArrayList.get(realPosition);
            if (data instanceof PrismPost){
                ((PrismPostViewHolder) holder).setData((PrismPost) data);
            } else if (data instanceof PrismUser) {
                ((PrismUserViewHolder) holder).setData((PrismUser) data);
            }
        }
    }

    @Override
    public int getItemCount() {
        return prismDataArrayList.size();
    }

    private int getRealPosition(int position) {
        if (RECYCLER_VIEW_AD_THRESHOLD == 0) {
            return position;
        } else {
            return position - position / RECYCLER_VIEW_AD_THRESHOLD;
        }
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

            populateUIElements();
        }

        /**
         * Set data for the PrismPostViewHolder UI elements
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
                    /* case LIKE_BY_FOLLOWINGS:
                        // TODO @MIKE
                        break;
                    case REPOST_BY_FOLLOWINGS:
                        // TODO @MIKE
                        break; */
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
                        Helper.intentToPrismPostDetailActivity(context, prismPost, prismPostImageView);
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
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                drawable.setCircular(true);
                                prismPostUserProfilePicture.setImageDrawable(drawable);

                                int whiteOutlinePadding = (int) (1 * Default.scale);
                                prismPostUserProfilePicture.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                prismPostUserProfilePicture.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                            }
                        });

                prismPostUserProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.intentToUserProfileActivity(context, prismPost.getPrismUser());
                    }
                });
            }

        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
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


        private PrismUserViewHolder(View itemView) {
            super(itemView);

            discoverPrismUserCardView = itemView.findViewById(R.id.discover_prism_user_card_view);
            discoverPrismUserImageView = itemView.findViewById(R.id.discover_prism_user_profile_picture_image_view);
            discoverPrismUserFollowButton = itemView.findViewById(R.id.discover_prism_user_follow_user_button);
            discoverPrismUserUsername = itemView.findViewById(R.id.discover_prism_user_username_text_view);
            discoverPrismUserName = itemView.findViewById(R.id.discover_prism_user_name_text_view);

            populateUIElements();
        }

        /**
         * Set data for the PrismPostViewHolder UI elements
         */
        public void setData(PrismUser prismUser) {
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

                discoverPrismUserUsername.setTypeface(Default.sourceSansProBold);
                discoverPrismUserName.setTypeface(Default.sourceSansProLight);

                discoverPrismUserUsername.setSelected(true);
                discoverPrismUserName.setSelected(true);
            }
        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
            // Setup Typefaces for all text based UI elements

        }
    }

    public class GoogleAdViewHolder extends RecyclerView.ViewHolder {

        private TextView sponsoredAdTextView;
        private AdView adView;
        private AdRequest adRequest;

        public GoogleAdViewHolder(View itemView) {
            super(itemView);

            sponsoredAdTextView = itemView.findViewById(R.id.discover_prism_post_user_sponsored_ad_text_view);
            adView = itemView.findViewById(R.id.discover_prism_post_google_ad_view);

            sponsoredAdTextView.setTypeface(Default.sourceSansProBold);

            new AdViewTask().execute();
        }

        /**
         * Set data for the GoogleAdViewHolder UI elements
         */
        public void setData() {

        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
            // Setup Typefaces for all text based UI elements

        }


        private class AdViewTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                        adView.loadAd(adRequest);
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
            }

        }

    }

}
