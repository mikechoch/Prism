package com.mikechoch.prism.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;

import java.util.ArrayList;

public class SearchDiscoverRecyclerViewAdapter extends RecyclerView.Adapter<SearchDiscoverRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PrismPost> prismPostArrayList;


    public SearchDiscoverRecyclerViewAdapter(Context context, ArrayList<PrismPost> prismPostArrayList) {
        this.context = context;
        this.prismPostArrayList = prismPostArrayList;
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

        private PrismPost prismPost;


        private ViewHolder(View itemView) {
            super(itemView);

            prismPostImageView = itemView.findViewById(R.id.discover_prism_post_image_view);

            populateUIElements();
        }

        /**
         * Set data for the ViewHolder UI elements
         */
        public void setData(PrismPost prismPost) {
            this.prismPost = prismPost;
        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
            // Setup Typefaces for all text based UI elements

            if (prismPost != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismPost.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(prismPostImageView);
            }
        }
    }

}
