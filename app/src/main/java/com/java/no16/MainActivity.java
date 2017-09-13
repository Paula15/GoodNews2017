package com.java.no16;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.java.no16.protos.NewsException;
import com.java.no16.service.CacheService;
import com.java.no16.service.GetNewsDetailService;
import com.java.no16.service.GetNewsListService;
import com.java.no16.service.GetSearchResultService;
import com.java.no16.ui.newslist.NewsListFragment;
import com.java.no16.ui.tablist.TabListFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabListFragment mTabListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabListFragment = new TabListFragment();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.menu_toolbar_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        onSearch();
                        break;
                    case R.id.action_settings:
                        onSettings();
                        break;
                    case R.id.action_favorite:
                        onFavorite();
                        break;
                }
                return true;
            }
        });

        GetNewsListService.initService();
        GetNewsDetailService.initService();
        GetSearchResultService.initService();
        CacheService.initService(this);

        gotoTabList();
    }

    private void onSearch() {
        final NewsListFragment fragment = mTabListFragment.getCurrentFragment();
        new MaterialDialog.Builder(this)
                .title(R.string.title_search)
                .widgetColorRes(R.color.colorPrimary)
                .positiveColorRes(R.color.colorPrimary)
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (!TextUtils.isEmpty(input)) {
                            fragment.doSearch(input.toString());
                        } else {
                            fragment.doSearch("");
                        }
                    }
                }).show();
    }

    private void onSettings() {
        // TODO(zpzhou)
    }

    private void onFavorite() {
        // TODO(zpzhou)
    }

    private void gotoTabList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mTabListFragment).commit();
    }

    @Override
    protected void onDestroy() {
        CacheService.closeService(this);
        Log.e(NewsException.FORCE_EXIT_ERROR, NewsException.FORCE_EXIT_MESSAGE);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        CacheService.closeService(this);
        Log.i(NewsException.EXIT_INFO, NewsException.EXIT_MESSAGE);
        super.onStop();
    }

}
