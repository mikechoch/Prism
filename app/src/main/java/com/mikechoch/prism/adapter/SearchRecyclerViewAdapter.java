package com.mikechoch.prism.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.mikechoch.prism.activity.PrismTagActivity;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        Object item = items.get(position);
        if (item instanceof PrismUser) {
            return 0;
        } else if (item instanceof String) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new PeopleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_search_type_recycler_view_item_layout, parent, false));
            case 1:
                return new TagViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.tag_search_type_recycler_view_item_layout, parent, false));
            default:
                return new PeopleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_search_type_recycler_view_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                PeopleViewHolder peopleViewHolder = (PeopleViewHolder) holder;
                peopleViewHolder.setData((PrismUser) items.get(position));
                break;
            case 1:
                TagViewHolder tagViewHolder = (TagViewHolder) holder;
                tagViewHolder.setData((String) items.get(position));
                break;
        }
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout peopleLinearLayout;
        private ImageView peopleProfilePictureImageView;
        private TextView peopleusernameTextView;
        private TextView peopleNameTextView;

        private PrismUser prismUser;

        PeopleViewHolder(View itemView) {
            super(itemView);

            peopleLinearLayout = itemView.findViewById(R.id.people_linear_layout);
            peopleProfilePictureImageView = itemView.findViewById(R.id.people_profile_picture_image_view);
            peopleusernameTextView = itemView.findViewById(R.id.people_username_text_view);
            peopleNameTextView = itemView.findViewById(R.id.people_name_text_view);
        }

        /**
         *
         * @param prismUser
         */
        public void setData(PrismUser prismUser) {
            this.prismUser = prismUser;
            populateInterfaceElements();
        }

        /**
         *
         */
        private void populateInterfaceElements() {
            peopleusernameTextView.setText(prismUser.getUsername());
            peopleNameTextView.setText(prismUser.getFullName());

            peopleusernameTextView.setTypeface(Default.sourceSansProBold);
            peopleNameTextView.setTypeface(Default.sourceSansProLight);

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
                    Helper.intentToUserProfileActivity(context, prismUser);
                }
            });
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
         *
         * @param tag
         */
        public void setData(String tag) {
            this.tag = tag;
            populateInterfaceElements();
        }

        /**
         *
         */
        private void populateInterfaceElements() {
            tagNameTextView.setText(tag);
            fetchPostsCountUnderTag(tagPostCountTextView);

            tagNameTextView.setTypeface(Default.sourceSansProBold);
            tagPostCountTextView.setTypeface(Default.sourceSansProLight);

            tagLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tagIntent = new Intent(context, PrismTagActivity.class);
                    tagIntent.putExtra(Default.CLICKED_TAG_EXTRA, tag);
                    context.startActivity(tagIntent);
                }
            });
        }

        /**
         * @param tagPostCountTextView
         */
        private void fetchPostsCountUnderTag(TextView tagPostCountTextView) {
            DatabaseReference tagsReference = Default.TAGS_REFERENCE;
            tagsReference.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tagPostCountTextView.setText(dataSnapshot.getChildrenCount() + " posts");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
