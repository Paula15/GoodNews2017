package com.java.no16.supplier;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.NewsDetail;
import com.java.no16.service.GetNewsDetailService;

/**
 * Created by songshihong on 11/09/2017.
 */

public class NewsDetailSupplier implements Supplier<Result<NewsDetail>> {
    String key;

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public Result<NewsDetail> get() {
        NewsDetail newsDetail = null;
        newsDetail = GetNewsDetailService.getNewsDetail(key);
        if (newsDetail == null) {
            return Result.failure();
        } else {
            return Result.success(newsDetail);
        }
    }
}
