package com.mikechoch.prism.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikechoch.prism.fire.callback.OnFetchCallback;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.SearchActivity;
import com.mikechoch.prism.adapter.SearchDiscoverRecyclerViewAdapter;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.DiscoverController;
import com.mikechoch.prism.fire.callback.OnInitializeDiscoveryCallback;
import com.mikechoch.prism.type.Discovery;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by mikechoch on 1/22/18.
 */

public class SearchFragment extends Fragment {

    public static LinearLayout searchLinearLayout;
    private CardView searchCardView;
    private TextView searchBarHintTextView;

    private static HashMap<Discovery, SearchDiscoverRecyclerViewAdapter> discoveryHorizontalRecyclerViewAdapterHashMap;
    private static HashMap<Discovery, LinearLayout> discoveryLinearLayoutHashMap;

    private static HashMap<Discovery, OnFetchCallback> discoveryOnFetchListenerHashMap;

    private static ArrayList<Object> prismUsers;
    private static ArrayList<Object> likedPrismPosts;
    private static ArrayList<Object> repostedPrismPosts;
    private static ArrayList<Object> prismTags;


    public static final SearchFragment newInstance() {
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
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
            public void onFailure() {

            }
        });

        return view;
    }

    /**
     * @param context
     * @param discovery
     */
    public static void addDiscoveryRecyclerView(Context context, Discovery discovery) {
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

                TextView sponsoredAdTextView = googleAdView.findViewById(R.id.discover_prism_post_user_sponsored_ad_text_view);
                AdView adView = googleAdView.findViewById(R.id.discover_prism_post_google_ad_view);
                sponsoredAdTextView.setTypeface(Default.sourceSansProBold);
                new AdViewTask().execute(context, adView);

                searchLinearLayout.addView(googleAdView);

                break;
            case REPOST:
                OnFetchCallback repostedPrismPostOnFetchCallback = updateDiscoveryItem(context, discovery, repostedPrismPosts);
                discoveryOnFetchListenerHashMap.put(discovery, repostedPrismPostOnFetchCallback);
                repostedPrismPostOnFetchCallback.onSuccess(new ArrayList<>());
                DiscoverController.generateHighestRepostedPosts(repostedPrismPostOnFetchCallback);
                break;
            case TAG:
                OnFetchCallback tagsOnFetchCallback = updateDiscoveryItem(context, discovery, prismTags);
                discoveryOnFetchListenerHashMap.put(discovery, tagsOnFetchCallback);
                tagsOnFetchCallback.onSuccess(new ArrayList<>());
//                DiscoverController.getListOfPrismPostsForRandomTag(tagsOnFetchCallback);
                break;
        }
    }

    /**
     * @param context
     * @param discovery
     * @param arrayList
     * @return
     */
    private static OnFetchCallback updateDiscoveryItem(Context context, Discovery discovery, ArrayList<Object> arrayList) {
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

    private static class AdViewTask extends AsyncTask<Object, Void, Void> {

        private AdRequest adRequest;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... params) {
            Context asyncContext = (Context) params[0];
            AdView adView = (AdView) params[1];
            ((Activity) asyncContext).runOnUiThread(new Runnable() {
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
