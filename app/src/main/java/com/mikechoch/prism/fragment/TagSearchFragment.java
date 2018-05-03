package com.mikechoch.prism.fragment;

import android.os.Bundle;
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

    public static RecyclerView tagRecyclerView;
    public static SearchRecyclerViewAdapter tagSearchRecyclerViewAdapter;

    public static final TagSearchFragment newInstance() {
        TagSearchFragment tagSearchFragment = new TagSearchFragment();
        return tagSearchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_type_fragment_layout, container, false);

        tagRecyclerView = view.findViewById(R.id.search_type_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.recycler_view_divider));
        tagRecyclerView.setLayoutManager(linearLayoutManager);
        tagRecyclerView.setItemAnimator(defaultItemAnimator);
        tagRecyclerView.addItemDecoration(dividerItemDecoration);

        tagSearchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getActivity(), SearchActivity.hashTagsCollection);
        tagRecyclerView.setAdapter(tagSearchRecyclerViewAdapter);

        return view;
    }

}
