package com.mikechoch.prism.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.PeopleSearchFragment;
import com.mikechoch.prism.fragment.TagSearchFragment;


public class SearchTypeViewPagerAdapter extends FragmentStatePagerAdapter {


    public SearchTypeViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return Default.SEARCH_TYPE_VIEW_PAGER_SIZE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PeopleSearchFragment.newInstance();
            case 1:
                return TagSearchFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
