package com.java.no16.ui.tablist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.service.GetNewsListService;

import java.util.List;

/**
 * Created by zhou9 on 2017/9/12.
 */

public class TabListFragment extends Fragment {

    List<Category> mCategoryList, mUnusedCategoryList;
    SlidingTabLayout mTabLayout;
    ViewPager mPager;
    TabListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tablist, null);

        // Order makes sense!!!
        initCategoryList();
        initAdapter(view);
        initPager(view);
        initTabLayout(view);

        return view;
    }

    private void initCategoryList() {
        mCategoryList = GetNewsListService.getCategoryList();
        mUnusedCategoryList = GetNewsListService.getUnusedCategoryList();
    }

    private void initAdapter(View view) {
        mAdapter = new TabListAdapter(getFragmentManager(), mCategoryList);
    }

    private void initPager(View view) {
        mPager = (ViewPager) view.findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);
    }

    private void initTabLayout(View view) {
        mTabLayout = (SlidingTabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setViewPager(mPager);
        mTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.colorPrimary);
            }

            @Override
            public int getDividerColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.light_grey);
            }
        });
    }

}
