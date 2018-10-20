package com.mikechoch.prism.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.MainFeedFragment;
import com.mikechoch.prism.fragment.NotificationFragment;
import com.mikechoch.prism.fragment.ProfileFragment;
import com.mikechoch.prism.fragment.SearchFragment;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {


    public MainViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return Default.MAIN_VIEW_PAGER_SIZE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Default.MAIN_VIEW_PAGER_MAIN_FEED:
                return MainFeedFragment.newInstance();
            case Default.MAIN_VIEW_PAGER_SEARCH:
                return SearchFragment.newInstance();
            case Default.MAIN_VIEW_PAGER_NOTIFICATIONS:
                return NotificationFragment.newInstance();
            case Default.MAIN_VIEW_PAGER_PROFILE:
                return ProfileFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
