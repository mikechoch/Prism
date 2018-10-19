package com.mikechoch.prism.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.SearchActivity;
import com.mikechoch.prism.adapter.SearchRecyclerViewAdapter;


public class TagSearchFragment extends Fragment {

    private RecyclerView tagRecyclerView;
    private SearchRecyclerViewAdapter tagSearchRecyclerViewAdapter;


    public static TagSearchFragment newInstance() {
        return new TagSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_search_type_fragment_layout, container, false);

        tagRecyclerView = view.findViewById(R.id.tag_search_type_recycler_view);

        setupInterfaceElements();

        return view;
    }

    /**
     * Setup the tag search fragment recycler view
     * Based on a query in the search bar, the tag showing will dynamically change
     */
    private void setupTagSearchRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
        tagRecyclerView.setLayoutManager(linearLayoutManager);
        tagRecyclerView.setItemAnimator(defaultItemAnimator);
        tagRecyclerView.addItemDecoration(dividerItemDecoration);

        tagSearchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getActivity(), SearchActivity.hashTagsCollection);
        tagRecyclerView.setAdapter(tagSearchRecyclerViewAdapter);
    }

    /**
     * Setup elements of current fragment
     */
    private void setupInterfaceElements() {

        setupTagSearchRecyclerView();
    }

}
