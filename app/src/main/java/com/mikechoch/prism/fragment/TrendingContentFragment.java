package com.mikechoch.prism.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikechoch.prism.R;

/**
 * Created by mikechoch on 1/22/18.
 */

public class TrendingContentFragment extends Fragment {


    public static final TrendingContentFragment newInstance() {
        TrendingContentFragment trendingContentFragment = new TrendingContentFragment();
        return trendingContentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_content_fragment_layout, container, false);
        return view;
    }

}
