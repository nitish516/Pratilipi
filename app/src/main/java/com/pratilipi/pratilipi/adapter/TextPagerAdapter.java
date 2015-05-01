package com.pratilipi.pratilipi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


/**
 * Created by Nitish on 03-04-2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pratilipi.pratilipi.PageFragment;

import java.util.List;

public class TextPagerAdapter extends FragmentStatePagerAdapter {
    private final List<CharSequence> pageTexts;
    private float fontSize;

    public TextPagerAdapter(FragmentManager fm, List<CharSequence> pageTexts, float fontSize) {
        super(fm);
        this.pageTexts = pageTexts;
        this.fontSize = fontSize;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.newInstance(pageTexts.get(i), fontSize);
    }

    @Override
    public int getCount() {
        return pageTexts.size();
    }

    public void onPageSelected(int index) {

        if (index == getCount() - 1) {
            // communicator.refreshViewPager();
        }
    }
}
