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

    public static SearchDiscoverRecyclerViewAdapter[] searchDiscoverRecyclerViewAdapters = new SearchDiscoverRecyclerViewAdapter[2];


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
     */
    public static void createAllDiscoveryRecyclerViews(Context context) {
        for (int i = 0; i < 2; i++) {
            TextView recyclerViewTitleTextView = new TextView(context);
            LinearLayout.LayoutParams titleTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            titleTextViewLayoutParams.setMargins((int) (4 * Default.scale), (int) (4 * Default.scale), 0, 0);
            recyclerViewTitleTextView.setLayoutParams(titleTextViewLayoutParams);
            recyclerViewTitleTextView.setTextSize(15f);
            recyclerViewTitleTextView.setTextColor(Color.WHITE);
            recyclerViewTitleTextView.setTypeface(Default.sourceSansProLight);

            RecyclerView prismPostDiscoveryRecyclerView = new RecyclerView(context);
            LinearLayout.LayoutParams recyclerViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            prismPostDiscoveryRecyclerView.setLayoutParams(recyclerViewLayoutParams);
            LinearLayoutManager discoveryLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            DefaultItemAnimator discoveryDefaultItemAnimator = new DefaultItemAnimator();
            DividerItemDecoration discoveryDividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
            prismPostDiscoveryRecyclerView.setLayoutManager(discoveryLinearLayoutManager);
            prismPostDiscoveryRecyclerView.setItemAnimator(discoveryDefaultItemAnimator);
            prismPostDiscoveryRecyclerView.addItemDecoration(discoveryDividerItemDecoration);
            prismPostDiscoveryRecyclerView.setNestedScrollingEnabled(false);

            SearchDiscoverRecyclerViewAdapter searchDiscoverRecyclerViewAdapter = null;
            switch (i) {
                case 0:
                    recyclerViewTitleTextView.setText("Most Liked");
                    searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, DiscoverController.generateHighestLikedPosts());
                    break;
                case 1:
                    recyclerViewTitleTextView.setText("Most Reposted");
                    searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, DiscoverController.generateHighestRepostedPosts());
                    break;
            }
            prismPostDiscoveryRecyclerView.setAdapter(searchDiscoverRecyclerViewAdapter);
            searchDiscoverRecyclerViewAdapters[i] = searchDiscoverRecyclerViewAdapter;

            searchLinearLayout.addView(recyclerViewTitleTextView);
            searchLinearLayout.addView(prismPostDiscoveryRecyclerView);
        }
    }
}
