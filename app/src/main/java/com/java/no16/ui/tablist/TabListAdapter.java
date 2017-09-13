package com.java.no16.ui.tablist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.java.no16.protos.Category;
import com.java.no16.ui.newslist.NewsListFragment;

import java.util.List;
import java.util.Objects;

/**
 * Created by zhou9 on 2017/9/12.
 */

public class TabListAdapter extends FragmentStatePagerAdapter {
    private List<Category> mCategoryList;

    public TabListAdapter(FragmentManager fm, List<Category> categoryList) {
        super(fm);
        this.mCategoryList = categoryList;
    }

    @Override
    public Fragment getItem(int position) {
        Category category = mCategoryList.get(position);
        Log.e("@" + Thread.currentThread().getName() + " => " + category.getName(), "getItem");
        return NewsListFragment.newInstance(category);
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mCategoryList.get(position).getName();
    }

    public void updateData(List<Category> categoryList) {
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }
}
