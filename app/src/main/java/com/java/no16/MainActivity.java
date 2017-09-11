package com.java.no16;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.java.no16.service.CacheService;
import com.java.no16.service.GetNewsDetailService;
import com.java.no16.service.GetNewsListService;
import com.java.no16.service.GetSearchResultService;
import com.java.no16.ui.newslist.NewsListFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        GetNewsListService.initService();
        GetNewsDetailService.initService();
        GetSearchResultService.initService();
        CacheService.initService();

        gotoNewsList();
    }

    private void gotoNewsList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new NewsListFragment()).commit();
        mToolbar.setTitle(R.string.title_news);
    }
}
