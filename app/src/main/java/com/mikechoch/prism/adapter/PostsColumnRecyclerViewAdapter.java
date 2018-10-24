package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.IntentHelper;

import java.util.ArrayList;
import java.util.List;


public class PostsColumnRecyclerViewAdapter extends RecyclerView.Adapter<PostsColumnRecyclerViewAdapter.PrismPostViewHolder> {

    private Context context;
    private List<PrismPost> prismPostsArrayList;


    public PostsColumnRecyclerViewAdapter(Context context, ArrayList<PrismPost> prismPostsArrayList) {
        this.context = context;
        this.prismPostsArrayList = prismPostsArrayList;
    }

    @NonNull
    @Override
    public PrismPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrismPostViewHolder(LayoutInflater.from(context).inflate(R.layout.post_column_recycler_view_item_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull PrismPostViewHolder holder, int position) {
        holder.setData(prismPostsArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.prismPostsArrayList.size();
    }


    public class PrismPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView userPostImageView;
        private ImageView userPostRepostedIcon;
        private ProgressBar progressBar;

        private PrismPost prismPost;


        PrismPostViewHolder(View itemView) {
            super(itemView);

            userPostImageView = itemView.findViewById(R.id.user_post_image_view);
            userPostRepostedIcon = itemView.findViewById(R.id.user_post_reposted_indicator);
            progressBar = itemView.findViewById(R.id.user_post_progress_bar);
        }

        /**
         * Set data for the PrismPostViewHolder interface elements
         */
        public void setData(PrismPost prismPost) {
            this.prismPost = prismPost;
            populateInterfaceElements();
        }

        /**
         * Populate userPostImageView using Glide with the specific post image
         */
        private void setupPostImageView() {
            userPostImageView.setMaxHeight((int) (Default.scale * 150));

            Glide.with(context)
                    .asBitmap()
                    .load(prismPost.getImage())
                    .apply(new RequestOptions().fitCenter().override((int) (Default.scale * 200)))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // TODO: @Mike should we display a toast or something here?
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(userPostImageView);

            int repostIconVisible = prismPost.getReposted() ? View.VISIBLE : View.GONE;
            userPostRepostedIcon.setVisibility(repostIconVisible);

            userPostImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToPrismPostDetailActivity(context, prismPost);
                }
            });
        }

        /**
         * Populate elements of current PrismPostViewHolder item
         */
        private void populateInterfaceElements() {
            setupPostImageView();
        }
    }
}