package com.java.no16.ui.newsdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.java.no16.R;
import com.java.no16.protos.NewsDetail;
import com.java.no16.supplier.NewsDetailSupplier;
import com.java.no16.ui.common.BaseActivity;
import com.java.no16.util.ThreadPool;

/**
 * Created by songshihong on 11/09/2017.
 */

public class NewsDetailActivity extends BaseActivity implements Updatable {
    Repository<Result<NewsDetail>> repository;
    NewsDetailObservable observable;
    Toolbar toolbar;
    ImageView imageView;
    View titleTextView;
    TextView contentTextView;
    NewsDetailSupplier newsDetailSupplier;
    public static final String NEWS = "news_key";

    private String newsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        initView();
        initRepository();
    }

    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.ivImage);
        titleTextView = (View) findViewById(R.id.titleText);
        contentTextView = (TextView) findViewById(R.id.contentText);
        newsDetailSupplier = new NewsDetailSupplier();
        newsDetail = getIntent().getStringExtra(NEWS);
        newsDetailSupplier.setKey(newsDetail);
    }

    @Override
    protected void initRepository() {
        newsDetailSupplier = new NewsDetailSupplier();
        repository = Repositories.repositoryWithInitialValue(Result.<NewsDetail>absent())
                .observe()
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(newsDetailSupplier)
                .compile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsDetailSupplier.setKey(newsDetail);
        repository.addUpdatable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        repository.removeUpdatable(this);
    }

    @Override
    public void update() {
        if (repository.get().isPresent()) {
            repository.get().ifFailedSendTo(new Receiver<Throwable>() {
                @Override
                public void accept(@NonNull Throwable value) {
                    Toast.makeText(NewsDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }).ifSucceededSendTo(new Receiver<NewsDetail>() {
                @Override
                public void accept(@NonNull final NewsDetail value) {
                    newsDetail = value.getNewsId();
                    toolbar.setTitle("ToolBar");
                    for (String image: value.getImageUrls()) {
                        Glide.with(NewsDetailActivity.this)
                                .load(image)
                                .asBitmap()
                                .into(imageView);
                    }
                    contentTextView.setText(value.getTitle() + '\n' + value.getContent());
                }
            });
        }
    }
}
