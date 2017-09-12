package com.java.no16.ui.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.java.no16.R;
import com.java.no16.service.CacheService;
import com.java.no16.supplier.SettingSupplier;
import com.java.no16.ui.common.BaseActivity;

/**
 * Created by songshihong on 11/09/2017.
 */

public class SettingActivity extends BaseActivity {
    private TextView shieldWords;
    private EditText writeShieldWord;
    private Button submit;
    private Switch isNightMode;
    private Switch isShowPicture;
    Repository<Result<CacheService>> repository;
    SettingSupplier supplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        initRepository();
    }

    @Override
    protected void initView() {
        shieldWords = (TextView) findViewById(R.id.shieldWords);
        writeShieldWord = (EditText) findViewById(R.id.writeShieldWord);
        submit = (Button) findViewById(R.id.submitShieldWord);
        isNightMode = (Switch) findViewById(R.id.isNightMode);
        isNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightMode.isChecked()) {
                    CacheService.setNight(true);
                } else {
                    CacheService.setNight(false);
                }
            }
        });
        isShowPicture = (Switch) findViewById(R.id.isShowPicture);
        isShowPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightMode.isChecked()) {
                    CacheService.setShowImage(true);
                } else {
                    CacheService.setShowImage(false);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String settedWord = String.valueOf(writeShieldWord.getText());
                if (settedWord.equals("")) {
                    Log.e("Error", "You cannot set an empty String!");
                    Toast.makeText(SettingActivity.this, "You cannot set an empty String!", Toast.LENGTH_SHORT).show();
                }
                else
                    CacheService.addKeywords(settedWord);
            }
        });
    }

    @Override
    protected void initRepository() {
        repository = Repositories.repositoryWithInitialValue(Result.<CacheService>absent())
                .observe()
                .onUpdatesPerLoop()
                .thenGetFrom(supplier)
                .compile();;
        updateView();
    }

    private void updateView() {
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
                        // TODO(bellasong): write css
                    } else {

                    }
                }
            });
        }
    }

}
