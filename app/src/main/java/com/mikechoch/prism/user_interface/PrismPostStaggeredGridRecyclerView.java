package com.mikechoch.prism.user_interface;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.PostsColumnRecyclerViewAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;

import java.util.ArrayList;

public class PrismPostStaggeredGridRecyclerView {

    public PrismPostStaggeredGridRecyclerView(Context context, LinearLayout recyclerViewContainer, ArrayList<PrismPost> prismPostArrayList) {
        recyclerViewContainer.removeAllViews();
        recyclerViewContainer.setWeightSum((float) Default.POSTS_COLUMNS);

//        ArrayList<ArrayList<PrismPost>> prismTagPostsArrayLists = new ArrayList<>(Collections.nCopies(userUploadedColumns, new ArrayList<>()));
        // TODO: figure out how to initialize an ArrayList of ArrayLists without using while loop inside of populating for-loop
        ArrayList<ArrayList<PrismPost>> prismTagPostsArrayLists = new ArrayList<>();
        for (int i = 0; i < prismPostArrayList.size(); i++) {
            while (prismTagPostsArrayLists.size() != Default.POSTS_COLUMNS) {
                prismTagPostsArrayLists.add(new ArrayList<>());
            }
            prismTagPostsArrayLists.get((i % Default.POSTS_COLUMNS)).add(prismPostArrayList.get(i));
        }

        for (int i = 0; i < Default.POSTS_COLUMNS; i++) {
            LinearLayout recyclerViewLinearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams one_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            recyclerViewLinearLayout.setLayoutParams(one_params);

            RecyclerView tagPostsRecyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.posts_recycler_view, null);
            LinearLayoutManager recyclerViewLinearLayoutManager = new LinearLayoutManager(context);
            tagPostsRecyclerView.setLayoutManager(recyclerViewLinearLayoutManager);
            PostsColumnRecyclerViewAdapter tagPostsColumnRecyclerViewAdapter = new PostsColumnRecyclerViewAdapter(context, prismTagPostsArrayLists.get(i));
            tagPostsRecyclerView.setAdapter(tagPostsColumnRecyclerViewAdapter);

            recyclerViewLinearLayout.addView(tagPostsRecyclerView);
            recyclerViewContainer.addView(recyclerViewLinearLayout);
        }

    }



}
