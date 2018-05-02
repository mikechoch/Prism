package com.mikechoch.prism.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.PeopleSearchFragment;
import com.mikechoch.prism.fragment.TagSearchFragment;

public class SearchTypeViewPagerAdapter extends FragmentStatePagerAdapter {

    /*
     * Global variables
     */
    private int NUM_ITEMS = Default.SEARCH_TYPE_VIEW_PAGER_SIZE;


    public SearchTypeViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PeopleSearchFragment().newInstance();
            case 1:
                return new TagSearchFragment().newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
