package com.mikechoch.prism.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.SearchActivity;
import com.mikechoch.prism.adapter.SearchDiscoverRecyclerViewAdapter;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DiscoverController;
import com.mikechoch.prism.callback.fetch.OnFetchCallback;
import com.mikechoch.prism.callback.action.OnInitializeDiscoveryCallback;
import com.mikechoch.prism.type.Discovery;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchFragment extends Fragment {

    private LinearLayout searchLinearLayout;
    private CardView searchCardView;
    private TextView searchBarHintTextView;

    private static HashMap<Discovery, SearchDiscoverRecyclerViewAdapter> discoveryHorizontalRecyclerViewAdapterHashMap;
    private static HashMap<Discovery, LinearLayout> discoveryLinearLayoutHashMap;

    private static HashMap<Discovery, OnFetchCallback> discoveryOnFetchListenerHashMap;

    private static ArrayList<Object> prismUsers;
    private static ArrayList<Object> likedPrismPosts;
    private static ArrayList<Object> repostedPrismPosts;
    private static ArrayList<Object> prismTags;


    public static SearchFragment newInstance() {
        return new SearchFragment();
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

        likedPrismPosts = new ArrayList<>();
        prismUsers = new ArrayList<>();
        repostedPrismPosts = new ArrayList<>();
        prismTags = new ArrayList<>();

        discoveryHorizontalRecyclerViewAdapterHashMap = new HashMap<>();
        discoveryLinearLayoutHashMap = new HashMap<>();
        discoveryOnFetchListenerHashMap = new HashMap<>();

        DiscoverController.setupDiscoverContent(new OnInitializeDiscoveryCallback() {
            @Override
            public void onSuccess() {
                for (Discovery discovery : Discovery.values()) {
                    addDiscoveryRecyclerView(getActivity(), discovery);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    /**
     * @param context
     * @param discovery
     */
    private void addDiscoveryRecyclerView(Context context, Discovery discovery) {
        switch (discovery) {
            case LIKE:
                OnFetchCallback likedPrismPostOnFetchCallback = updateDiscoveryItem(context, discovery, likedPrismPosts);
                discoveryOnFetchListenerHashMap.put(discovery, likedPrismPostOnFetchCallback);
                DiscoverController.generateHighestLikedPosts(likedPrismPostOnFetchCallback);
                break;
            case USER:
                OnFetchCallback prismUsersOnFetchCallback = updateDiscoveryItem(context, discovery, prismUsers);
                discoveryOnFetchListenerHashMap.put(discovery, prismUsersOnFetchCallback);
                prismUsersOnFetchCallback.onSuccess(new ArrayList<>());
//                DiscoverController.getListOfRandomPrismUsers(prismUsersOnFetchCallback);

                View googleAdView = LayoutInflater.from(context).inflate(
                        R.layout.discover_prism_post_google_ad_recycler_view_item_layout, null, false);

                LinearLayout sponsoredLinearLayout = googleAdView.findViewById(R.id.discover_prism_post_google_ad_sponsored_linear_layout);
                TextView sponsoredAdTextView = googleAdView.findViewById(R.id.discover_prism_post_user_sponsored_ad_text_view);
                AdView adView = googleAdView.findViewById(R.id.discover_prism_post_google_ad_view);
                ProgressBar adProgressBar = googleAdView.findViewById(R.id.discover_prism_post_google_ad_item_progress_bar);
                LinearLayout adFailedLinearLayout = googleAdView.findViewById(R.id.discover_prism_post_google_ad_item_failed_ad_layout);
                TextView adFailedTextView = googleAdView.findViewById(R.id.discover_prism_post_google_ad_item_failed_ad_layout_title);

                sponsoredAdTextView.setTypeface(Default.sourceSansProBold);
                adFailedTextView.setTypeface(Default.sourceSansProBold);

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        sponsoredLinearLayout.setVisibility(View.VISIBLE);
                        adView.setVisibility(View.VISIBLE);
                        adProgressBar.setVisibility(View.GONE);
                        adFailedLinearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        sponsoredLinearLayout.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        adProgressBar.setVisibility(View.GONE);
                        adFailedLinearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });

                searchLinearLayout.addView(googleAdView);

                break;
            case REPOST:
                OnFetchCallback repostedPrismPostOnFetchCallback = updateDiscoveryItem(context, discovery, repostedPrismPosts);
                discoveryOnFetchListenerHashMap.put(discovery, repostedPrismPostOnFetchCallback);
                repostedPrismPostOnFetchCallback.onSuccess(new ArrayList<>());
                DiscoverController.generateHighestRepostedPosts(repostedPrismPostOnFetchCallback);
                break;
            case TAG:
                discovery.setTitle(DiscoverController.randomTag);
                OnFetchCallback tagsOnFetchCallback = updateDiscoveryItem(context, discovery, prismTags);
                discoveryOnFetchListenerHashMap.put(discovery, tagsOnFetchCallback);
//                tagsOnFetchCallback.onSuccess(new ArrayList<>());
                DiscoverController.generateRandomPostsForHashTag(tagsOnFetchCallback);
                break;
        }
    }

    /**
     * @param context
     * @param discovery
     * @param arrayList
     * @return
     */
    private OnFetchCallback updateDiscoveryItem(Context context, Discovery discovery, ArrayList<Object> arrayList) {
        if (!discoveryHorizontalRecyclerViewAdapterHashMap.containsKey(discovery)) {
            LinearLayout propertyRecyclerViewLinearLayout = createDiscoveryRecyclerView(context, discovery, arrayList);
            searchLinearLayout.addView(propertyRecyclerViewLinearLayout);
        }

        return new OnFetchCallback() {
            @Override
            public void onSuccess(ArrayList<Object> fetchResults) {
                arrayList.clear();
                arrayList.addAll(fetchResults);
                discoveryHorizontalRecyclerViewAdapterHashMap.get(discovery).notifyDataSetChanged();
                int linearLayoutScrollViewVisibility = arrayList.size() > 0 ? View.VISIBLE : View.GONE;
                discoveryLinearLayoutHashMap.get(discovery).setVisibility(linearLayoutScrollViewVisibility);
//                if (FirebaseAction.loadingCount == 0) {
//                    searchBarProgressBar.setVisibility(View.GONE);
//                }
                refreshDiscoveryAdapters();
            }

            @Override
            public void onFailure() {

            }
        };
    }

    private static LinearLayout createDiscoveryRecyclerView(Context context, Discovery discovery, ArrayList<Object> arrayList) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View discoveryRecyclerViewLayout = layoutInflater.inflate(R.layout.search_discovery_recycler_view_layout, null, false);

        LinearLayout discoveryRecyclerViewLinearLayout = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view_linear_layout);
        ImageView recyclerViewTitleIcon = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view_title_icon);
        TextView recyclerViewTitleTextView = discoveryRecyclerViewLayout.findViewById(R.id.discovery_recycler_view_title_text_view);

        recyclerViewTitleIcon.setImageDrawable(context.getResources().getDrawable(discovery.getIcon()));
        recyclerViewTitleTextView.setText(discovery.getTitle());
        recyclerViewTitleTextView.setTypeface(Default.sourceSansProBold);

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

        SearchDiscoverRecyclerViewAdapter searchDiscoverRecyclerViewAdapter = new SearchDiscoverRecyclerViewAdapter(context, arrayList, discovery);
        prismPostDiscoveryRecyclerView.setAdapter(searchDiscoverRecyclerViewAdapter);
        discoveryHorizontalRecyclerViewAdapterHashMap.put(discovery, searchDiscoverRecyclerViewAdapter);
        discoveryLinearLayoutHashMap.put(discovery, discoveryRecyclerViewLinearLayout);
        prismPostDiscoveryRecyclerView.setAdapter(searchDiscoverRecyclerViewAdapter);

        return discoveryRecyclerViewLinearLayout;
    }

    /**
     *
     */
    public static void refreshDiscoveryAdapters() {
        for (Discovery discovery : Discovery.values()) {
            SearchDiscoverRecyclerViewAdapter adapter = discoveryHorizontalRecyclerViewAdapterHashMap.get(discovery);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                int linearLayoutScrollViewVisibility = adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE;
                discoveryLinearLayoutHashMap.get(discovery).setVisibility(linearLayoutScrollViewVisibility);
            }
        }
    }

}
