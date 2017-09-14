package com.java.no16.ui.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.java.no16.R;
import com.java.no16.service.CacheService;
import com.java.no16.supplier.SettingSupplier;
import com.java.no16.ui.common.BaseActivity;

/**
 * Created by songshihong on 11/09/2017.
 */

public class SettingActivity extends BaseActivity implements Updatable {

    public static final String KEY_IS_SHOW_IMAGE = "is_show_image";

    private Switch isNightMode;
    private Switch isShowPicture;
    Repository<Result<CacheService>> repository;
    SettingSupplier supplier;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        initRepository();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView() {
        isNightMode = (Switch) findViewById(R.id.isNightMode);
        isNightMode.setChecked(CacheService.isNight());
        isNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightMode.isChecked()) {
                    CacheService.setNight(true);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.settings_layout);
                    linearLayout.setBackgroundColor(Color.parseColor("#007850"));
                } else {
                    CacheService.setNight(false);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.settings_layout);
                    linearLayout.setBackgroundColor(Color.parseColor("#86C2B7"));
                }
            }
        });
        isShowPicture = (Switch) findViewById(R.id.isShowPicture);
        isShowPicture.setChecked(CacheService.isShowPicture());
        isShowPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture.isChecked()) {
                    CacheService.setShowImage(true);
                } else {
                    CacheService.setShowImage(false);
                }
            }
        });
    }

    @Override
    protected void initRepository() {
        repository = Repositories.repositoryWithInitialValue(Result.<CacheService>absent())
                .observe()
                .onUpdatesPerLoop()
                .thenGetFrom(supplier)
                .compile();
    }

    @Override
    public void update() {
        if (repository.get().isPresent()) {
            repository.get().ifFailedSendTo(new Receiver<Throwable>() {
                @Override
                public void accept(@NonNull Throwable value) {
                    Toast.makeText(SettingActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }).ifSucceededSendTo(new Receiver<CacheService>() {
                @Override
                public void accept(@NonNull final CacheService value) {
                    if (value.isNight()) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.settings_layout);
                        linearLayout.setBackgroundColor(Color.parseColor("#000000"));
                    } else {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.settings_layout);
                        linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        onFinish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onFinish();
        super.onBackPressed();
    }

    private void onFinish() {
        Intent data = new Intent();
        data.putExtra(KEY_IS_SHOW_IMAGE, CacheService.isShowPicture());
        setResult(RESULT_OK, data);
    }

}
