package com.java.no16.ui.tablist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.service.GetNewsListService;
import com.java.no16.ui.tablist.tabedit.TabEditActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhou9 on 2017/9/12.
 */

public class TabListFragment extends Fragment {

    public static final int REQUEST_CODE_EDIT = 1;

    List<Category> mCategoryList, mUnusedCategoryList;
    SlidingTabLayout mTabLayout;
    ViewPager mPager;
    TabListAdapter mAdapter;
    ImageView mIconCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tablist, null);

        // Order makes sense!!!
        initCategoryList();
        initAdapter(view);
        initPager(view);
        initTabLayout(view);
        initButtons(view);

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
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                //设置最小宽度，使其可以在滑动一部分距离
                ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);
                slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + mIconCategory.getMeasuredWidth());
            }
        });
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

    private void initButtons(View view) {
        mIconCategory = (ImageView) view.findViewById(R.id.icon_category);
        mIconCategory.setClickable(true);
        mIconCategory.bringToFront();
        mIconCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabEditActivity.class);
                intent.putExtra(TabEditActivity.KEY_CATEGORY_LIST, (ArrayList<Category>) mCategoryList);
                intent.putExtra(TabEditActivity.KEY_UNUSED_CATEGORY_LIST, (ArrayList<Category>) mUnusedCategoryList);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCategoryList = (List<Category>) data.getSerializableExtra(TabEditActivity.KEY_CATEGORY_LIST);
        mUnusedCategoryList = (List<Category>) data.getSerializableExtra(TabEditActivity.KEY_UNUSED_CATEGORY_LIST);
        GetNewsListService.setCategoryList(mCategoryList);
        mAdapter.updateData(mCategoryList);
        mTabLayout.setViewPager(mPager);
        Log.e("ONACTIVITYRESULT!!!!!!", mCategoryList.toString());
    }
}
