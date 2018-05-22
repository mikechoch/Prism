package com.mikechoch.prism.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.SearchActivity;
import com.mikechoch.prism.adapter.SearchDiscoverRecyclerViewAdapter;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DiscoverController;


/**
 * Created by mikechoch on 1/22/18.
 */

public class SearchFragment extends Fragment {

    public static LinearLayout searchLinearLayout;
    private CardView searchCardView;
    private TextView searchBarHintTextView;

    public static SearchDiscoverRecyclerViewAdapter[] searchDiscoverRecyclerViewAdapters = new SearchDiscoverRecyclerViewAdapter[3];


    public static final SearchFragment newInstance() {
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);

        searchLinearLayout = view.findViewById(R.id.search_fragment_linear_Layout);
        searchCardView = view.findViewById(R.id.search_bar_card_view);
        searchBarHintTextView = view.findViewById(R.id.search_bar_hint_text_view);
        searchBarHintTextView.setTypeface(Default.sourceSansProLight);

        searchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        DiscoverController.setupDiscoverContent(getActivity());

        return view;
    }

    /**
     *
     * @param context
     * @param value
     */
    public static void createAllDiscoveryRecyclerViews(Context context, String value) {
        for (int i = 0; i < 3; i++) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View discoveryRecyclerViewLayout = layoutInflater.inflate(R.layout.search_discovery_recycler_view_layout, null, false);

            ImageView recyclerViewTitleIcon = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view_title_icon);

            TextView recyclerViewTitleTextView = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view_title_text_view);
            recyclerViewTitleTextView.setTypeface(Default.sourceSansProLight);

            RecyclerView prismPostDiscoveryRecyclerView = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view);
            LinearLayout.LayoutParams recyclerViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            prismPostDiscoveryRecyclerView.setLayoutParams(recyclerViewLayoutParams);
            LinearLayoutManager discoveryLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            DefaultItemAnimator discoveryDefaultItemAnimator = new DefaultItemAnimator();
            DividerItemDecoration discoveryDividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
            discoveryDividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.recycler_view_no_divider));
            prismPostDiscoveryRecyclerView.setLayoutManager(discoveryLinearLayoutManager);
            prismPostDiscoveryRecyclerView.setItemAnimator(discoveryDefaultItemAnimator);
            prismPostDiscoveryRecyclerView.addItemDecoration(discoveryDividerItemDecoration);

            SearchDiscoverRecyclerViewAdapter searchDiscoverRecyclerViewAdapter = null;
            switch (i) {
                case 0:
                    recyclerViewTitleIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.like_heart));
                    recyclerViewTitleTextView.setText("Most Liked");
                    searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, DiscoverController.generateHighestLikedPosts(), "Likes");
                    break;
                case 1:
                    recyclerViewTitleIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.repost_iris));
                    recyclerViewTitleTextView.setText("Most Reposted");
                    searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, DiscoverController.generateHighestRepostedPosts(), "Reposts");
                    break;
                case 2:
                    recyclerViewTitleIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pound_white_48dp));
                    recyclerViewTitleTextView.setText(value);
                    searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, DiscoverController.getListofPrismPostsForRandomTag(), "Tag");
                    break;
            }
            prismPostDiscoveryRecyclerView.setAdapter(searchDiscoverRecyclerViewAdapter);
            searchDiscoverRecyclerViewAdapters[i] = searchDiscoverRecyclerViewAdapter;

            searchLinearLayout.addView(discoveryRecyclerViewLayout);
        }
    }
}
