package com.java.no16.common;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by songshihong on 11/09/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract void initView();
    protected abstract void initRepository();
}
