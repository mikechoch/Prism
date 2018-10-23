package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;

import java.util.ArrayList;


public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PRISM_USER_VIEW_TYPE = 0;
    private final int PRISM_TAG_VIEW_TYPE = 1;

    private Context context;
    private ArrayList<Object> items;


    public SearchRecyclerViewAdapter(Context context, ArrayList<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = PRISM_USER_VIEW_TYPE;
        Object item = items.get(position);
        if (item instanceof PrismUser) {
            viewType = PRISM_USER_VIEW_TYPE;
        } else if (item instanceof String) {
            viewType = PRISM_TAG_VIEW_TYPE;
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PRISM_USER_VIEW_TYPE:
                return new PrismUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_search_type_recycler_view_item_layout, parent, false));
            case PRISM_TAG_VIEW_TYPE:
                return new TagViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.tag_search_type_recycler_view_item_layout, parent, false));
            default:
                return new PrismUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_search_type_recycler_view_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case PRISM_USER_VIEW_TYPE:
                PrismUserViewHolder prismUserViewHolder = (PrismUserViewHolder) holder;
                prismUserViewHolder.setData((PrismUser) items.get(position));
                break;
            case PRISM_TAG_VIEW_TYPE:
                TagViewHolder tagViewHolder = (TagViewHolder) holder;
                tagViewHolder.setData((String) items.get(position));
                break;
        }
    }


    public class PrismUserViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout peopleLinearLayout;
        private ImageView peopleProfilePictureImageView;
        private TextView peopleUsernameTextView;
        private TextView peopleNameTextView;

        private PrismUser prismUser;


        PrismUserViewHolder(View itemView) {
            super(itemView);

            peopleLinearLayout = itemView.findViewById(R.id.people_linear_layout);
            peopleProfilePictureImageView = itemView.findViewById(R.id.people_profile_picture_image_view);
            peopleUsernameTextView = itemView.findViewById(R.id.people_username_text_view);
            peopleNameTextView = itemView.findViewById(R.id.people_name_text_view);
        }

        /**
         * Set data for the PrismUserViewHolder interface elements
         * @param prismUser - PrismUser to populate current PrismUserViewHolder elements
         */
        public void setData(PrismUser prismUser) {
            this.prismUser = prismUser;
            populateInterfaceElements();
        }

        /**
         * Set the username TextView to PrismUser username
         * Set the name TextView to PrismUser full name
         * Populate the PrismUser profile picture ImageView with Glide
         * Set the onClick of the container to Intent to PrismUserProfileActivity
         */
        private void setupPrismUserItemLayout() {
            peopleUsernameTextView.setText(prismUser.getUsername());
            peopleNameTextView.setText(prismUser.getFullName());

            Glide.with(context)
                    .asBitmap()
                    .thumbnail(0.05f)
                    .load(prismUser.getProfilePicture().lowResUri)
                    .into(new BitmapImageViewTarget(peopleProfilePictureImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            if (!prismUser.getProfilePicture().isDefault) {
                                int whiteOutlinePadding = (int) (1 * Default.scale);
                                peopleProfilePictureImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                peopleProfilePictureImageView.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                            } else {
                                peopleProfilePictureImageView.setPadding(0, 0, 0, 0);
                                peopleProfilePictureImageView.setBackground(null);
                            }

                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            drawable.setCircular(true);
                            peopleProfilePictureImageView.setImageDrawable(drawable);
                        }
                    });

            peopleLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, prismUser);
                }
            });
        }

        /**
         * Setup elements of current PrismUserViewHolder item
         */
        private void populateInterfaceElements() {
            peopleUsernameTextView.setTypeface(Default.sourceSansProBold);
            peopleNameTextView.setTypeface(Default.sourceSansProLight);

            setupPrismUserItemLayout();
        }
    }


    public class TagViewHolder extends RecyclerView.ViewHolder  {

        private LinearLayout tagLinearLayout;
        private TextView tagNameTextView;
        private TextView tagPostCountTextView;

        private String tag;


        TagViewHolder(View itemView) {
            super(itemView);

            tagLinearLayout = itemView.findViewById(R.id.tag_linear_layout);
            tagNameTextView = itemView.findViewById(R.id.tag_name_text_view);
            tagPostCountTextView = itemView.findViewById(R.id.tag_posts_count_text_view);
        }

        /**
         * Set data for the TagViewHolder interface elements
         * @param tag - String tag to populate TagViewHolder elements
         */
        public void setData(String tag) {
            this.tag = tag;
            populateInterfaceElements();
        }

        /**
         * Set the text of the tag name TextVie to the tag String
         * Set the container onClick to Intent to TagActivity
         */
        private void setupTagItemLayout() {
            tagNameTextView.setText(tag);

            tagLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToTagActivity(context, tag);
                }
            });
        }

        /**
         * Setup the elements of the current TagViewHolder item
         */
        private void populateInterfaceElements() {
            tagNameTextView.setTypeface(Default.sourceSansProBold);
            tagPostCountTextView.setTypeface(Default.sourceSansProLight);

            setupTagItemLayout();
            fetchPostsCountUnderTag();
        }

        /**
         * TODO: Should this go in Firebase class?
         * Fetch the count of the current tag to show number posts under name TextView
         */
        private void fetchPostsCountUnderTag() {
            DatabaseReference tagsReference = Default.TAGS_REFERENCE;
            tagsReference.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //TODO: Log success?
                    String tagPostCountString = (int) dataSnapshot.getChildrenCount() +
                            Helper.getSingularOrPluralText("posts", (int) dataSnapshot.getChildrenCount());
                    tagPostCountTextView.setText(tagPostCountString);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO: Log Failure/Cancel?
                }
            });
        }
    }
}
