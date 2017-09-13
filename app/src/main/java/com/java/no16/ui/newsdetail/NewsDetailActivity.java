package com.java.no16.ui.newsdetail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.java.no16.R;
import com.java.no16.protos.NewsDetail;
import com.java.no16.service.CacheService;
import com.java.no16.supplier.NewsDetailSupplier;
import com.java.no16.ui.common.BaseActivity;
import com.java.no16.ui.setting.SettingActivity;
import com.java.no16.ui.share.ShareActivity;
import com.java.no16.util.ThreadPool;

/**
 * Created by songshihong on 11/09/2017.
 */

public class NewsDetailActivity extends BaseActivity implements Updatable {
    private Repository<Result<NewsDetail>> repository;
    private NewsDetailObservable observable;
    private ImageView imageView;
    private ObservableWebView contentWebView;
    private NewsDetailSupplier newsDetailSupplier;
    private ImageView nextImage;
    private Toolbar toolbar;
    private FloatingActionButton sound;
    public static final String NEWS = "news_key";
    private boolean isSound;
    private String content;
    private NewsDetail lastNewsDetail;
    private Menu menu;

    private String newsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        initView();
        initRepository();
        Log.e("Create", "gg");
    }

    protected void initView() {
        Log.e("View created", "hhh");
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        imageView = (ImageView) findViewById(R.id.ivImage);
        contentWebView = (ObservableWebView) findViewById(R.id.contentText);
        nextImage = (ImageView) findViewById(R.id.nextImage);
        setSupportActionBar(toolbar);
        SpeechUtility.createUtility(NewsDetailActivity.this, "appid=59b8a72d");
        isSound = false;
        mySynthesizer = SpeechSynthesizer.createSynthesizer(this, myInitListener);
        sound = (FloatingActionButton) findViewById(R.id.sound);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSound = !isSound;
                if (isSound) {
                    mySynthesizer.startSpeaking(content, mTtsListener);
                } else {
                    mySynthesizer.stopSpeaking();
                }
            }
        });
        newsDetailSupplier = new NewsDetailSupplier();
        newsDetail = getIntent().getStringExtra(NEWS);
        newsDetailSupplier.setKey(newsDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("Menu created", "233");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.news_detail_menu, menu);
        this.menu = menu;
        return true;
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
        Log.e("Initialing", "233");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "resume");
        newsDetailSupplier.setKey(newsDetail);
        repository.addUpdatable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        repository.removeUpdatable(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void update() {
        Log.e("Update:", "updating");
        Log.e("repository.isPresent", String.valueOf(repository.get().isPresent()));
        boolean color = CacheService.getFavorite(newsDetail);
        if (color) {
            menu.getItem(2).setIcon(R.drawable.ic_star_rate_white_18dp);
        }
        else {
            menu.getItem(2).setIcon(R.drawable.ic_star_rate_black_18dp);
        }
        if (repository.get().isPresent()) {
            repository.get().ifFailedSendTo(new Receiver<Throwable>() {
                @Override
                public void accept(@NonNull Throwable value) {
                    Log.e("Nothing", "GG");
                    Toast.makeText(NewsDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }).ifSucceededSendTo(new Receiver<NewsDetail>() {
                @Override
                public void accept(@NonNull final NewsDetail value) {
                    newsDetail = value.getNewsId();
                    lastNewsDetail = value;
                    Log.e("NewsDetail", newsDetail);
                    if (CacheService.isShowPicture()) {
                        Log.e("showPic", String.valueOf(CacheService.isShowPicture()));
                        nextImage.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        if (value.getImageUrlsCount() == 0) {
                        } else {
                            Glide.with(NewsDetailActivity.this)
                                    .load(value.getImageUrls().get(0))
                                    .asBitmap()
                                    .into(nextImage);
                            if (value.getImageUrlsCount() > 1) {
                                Glide.with(NewsDetailActivity.this)
                                        .load(value.getImageUrls().get(1))
                                        .asBitmap()
                                        .into(imageView);
                            }
                        }
                    } else {
                        nextImage.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    Log.e("isNight", String.valueOf(CacheService.isNight()));
                    content = value.getContent();
                    if (CacheService.isNight()) {
                        Log.e("title: ", value.getTitle());
                        Log.e("content: ", value.getContent());
                        String css = "<style>\n" +
                                "html, img, video {\n" +
                                "  -webkit-filter: invert(1) hue-rotate(180deg);\n" +
                                "  filter: invert(1) hue-rotate(180deg);\n" +
                                "}\n" +
                                "\n" +
                                "body {\n" +
                                "  background: black;\n" +
                                "}\n" +
                                "</style>";
                        String html = "<html> <head> " + css + "</head>" + "<body> <h1>" + value.getTitle() + "</h1><p>" + value.getContent() + "</p></body>";
                        contentWebView.loadDataWithBaseURL(null, html, "text/html; charset=UTF-8", null, null);
                    } else {
                        Log.e("title: ", value.getTitle());
                        Log.e("content: ", value.getContent());
                        String html = "<html> <head> " + "</head>" + "<body> <h2>" + value.getTitle() + "</h2><p>" + value.getContent() + "</p></body>";
                        contentWebView.loadDataWithBaseURL(null, html, "text/html; charset=UTF-8", null, null);
                    }
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(NewsDetailActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.menu_search:
                // TODO(bellasong):
                // Change the activity of class
                Toast.makeText(NewsDetailActivity.this, "Search selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.menu_star:
                // TODO(bellasong):
                // Change the activity of class
                Toast.makeText(NewsDetailActivity.this, "Star selected", Toast.LENGTH_SHORT).show();
                boolean color = CacheService.getFavorite(lastNewsDetail.getNewsId());
                if (color) {
                    menuItem.setIcon(R.drawable.ic_star_rate_black_18dp);
                } else {
                    menuItem.setIcon(R.drawable.ic_star_rate_white_18dp);
                }
                CacheService.setFavorite(lastNewsDetail.getNewsId(), !color);
                return true;
            case R.id.menu_share:
                Toast.makeText(NewsDetailActivity.this, "Share selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra("url", "www.baidu.com");
                intent.putExtra("title", lastNewsDetail.getTitle());
                intent.putExtra("content", lastNewsDetail.getContent());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    private SpeechSynthesizer mySynthesizer;

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("mySynthesiezer:", "InitListener init() code = " + code);
        }
    };

    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
}
