package com.pratilipi.pratilipi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by MOHIT KHAITAN on 12-06-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabsSumB) {
        super(fm);
        this.Titles = mTitles;
        this.NumOfTabs = mNumOfTabsSumB;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            MainActivity.HomeFragment homeActivityObj = new MainActivity.HomeFragment();
            return homeActivityObj;
        } else if (position == 1) {
            MainActivity.CategoriesFragment categoriesActivityObj = new MainActivity.CategoriesFragment();
            return categoriesActivityObj;
        } else if (position == 2) {
            MainActivity.ShelfFragment shelfActivityObj = new MainActivity.ShelfFragment();
            return shelfActivityObj;
        } else {
            ProfileFragment profileActivityObj = new ProfileFragment();
            return profileActivityObj;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }



    @Override
    public int getCount() {
        return NumOfTabs;
    }
}
