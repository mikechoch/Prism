package com.mikechoch.prism.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.LikedPostsFragment;
import com.mikechoch.prism.fragment.UploadedRepostedPostsFragment;

public class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    private int NUM_ITEMS = Default.USER_POSTS_VIEW_PAGER_SIZE;


    public ProfileViewPagerAdapter(FragmentManager fragmentManager) {
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
                return new UploadedRepostedPostsFragment().newInstance();
            case 1:
                return new LikedPostsFragment().newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
