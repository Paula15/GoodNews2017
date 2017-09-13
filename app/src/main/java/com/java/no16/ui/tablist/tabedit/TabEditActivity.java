package com.java.no16.ui.tablist.tabedit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.ui.tablist.helper.ItemDragHelperCallback;

/**
 * 频道 增删改查 排序
 * Created by YoKeyword on 15/12/29.
 */
public class TabEditActivity extends AppCompatActivity {

    // TODO(zpzhou): Save modification to database.

    public static final String KEY_CATEGORY_LIST = "category_list";
    public static final String KEY_UNUSED_CATEGORY_LIST = "unused_category_list";

    private RecyclerView mRecyclerView;
    private List<Category> mCategoryList, mUnusedCategoryList;

    TabEditAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabedit);

        mRecyclerView = (RecyclerView) findViewById(R.id.recy);
        initCategoryList();
        initAdapter();
    }

    private void initCategoryList() {
        mCategoryList = (List<Category>) getIntent().getSerializableExtra(KEY_CATEGORY_LIST);
        mUnusedCategoryList = (List<Category>) getIntent().getSerializableExtra(KEY_UNUSED_CATEGORY_LIST);
    }

    private void initAdapter() {
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);

        mAdapter = new TabEditAdapter(this, helper, mCategoryList, mUnusedCategoryList);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mAdapter.getItemViewType(position);
                return viewType == TabEditAdapter.TYPE_MY || viewType == TabEditAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnMyChannelItemClickListener(new TabEditAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(TabEditActivity.this, mCategoryList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mAdapter.onFinish();
        super.onBackPressed();
    }
}
