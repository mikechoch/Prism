package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;

public class SearchDiscoverRecyclerViewAdapter extends RecyclerView.Adapter<SearchDiscoverRecyclerViewAdapter.ViewHolder> {

    private int DISCOVER_LIKE_VIEW_TYPE = 0;
    private int DISCOVER_REPOST_VIEW_TYPE = 0;
    private int DISCOVER_TAG_VIEW_TYPE = 0;
    private int DISCOVER_SUGGESTED_PERSON_VIEW_TYPE = 0;

    private Context context;
    private ArrayList<PrismPost> prismPostArrayList;
    private String discoveryType;


    public SearchDiscoverRecyclerViewAdapter(Context context, ArrayList<PrismPost> prismPostArrayList, String discoveryType) {
        this.context = context;
        this.prismPostArrayList = prismPostArrayList;
        this.discoveryType = discoveryType;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.discover_prism_post_recycler_view_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(prismPostArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return prismPostArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView prismPostImageView;
        private TextView prismPostUsername;
        private TextView prismPostCount;
        private ImageView prismPostUserProfilePicture;


        private ViewHolder(View itemView) {
            super(itemView);

            prismPostImageView = itemView.findViewById(R.id.discover_prism_post_image_view);
            prismPostUsername = itemView.findViewById(R.id.discover_prism_post_user_text_view);
            prismPostCount = itemView.findViewById(R.id.discover_prism_post_date_count_text_view);
            prismPostUserProfilePicture = itemView.findViewById(R.id.discover_prism_post_profile_picture_image_view);

            populateUIElements();
        }

        /**
         * Set data for the ViewHolder UI elements
         */
        public void setData(PrismPost prismPost) {
            if (prismPost.getPrismUser() != null) {
                String username = prismPost.getPrismUser().getUsername();
                prismPostUsername.setText(username);
                prismPostUsername.setSelected(true);

                String fancyDate = Helper.getFancyDateDifferenceString(-1 * prismPost.getTimestamp());
                String countString = "";
                switch (discoveryType) {
                    case "Likes":
                        countString += prismPost.getLikes() + " likes";
                        prismPostCount.setText(fancyDate + " • " + countString);
                        break;
                    case "Reposts":
                        countString += prismPost.getReposts() + " reposts";
                        prismPostCount.setText(fancyDate + " • " + countString);
                        break;
                    case "Tag":
                        prismPostCount.setText(fancyDate);
                        break;
                    case "Suggested":

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

}
