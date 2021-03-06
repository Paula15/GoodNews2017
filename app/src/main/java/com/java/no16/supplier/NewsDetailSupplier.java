package com.java.no16.supplier;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;
import com.java.no16.service.GetNewsDetailService;

/**
 * Created by songshihong on 11/09/2017.
 */

public class NewsDetailSupplier implements Supplier<Result<NewsDetail>> {
    String key;
    static boolean isOffline;

    public void setKey(String key) {
        this.key = key;
    }
    public synchronized void setIsOffline(boolean line) {
        this.isOffline = line;
    }

    @NonNull
    @Override
    public Result<NewsDetail> get() {
        NewsDetail newsDetail = null;
        try {
            if (!this.isOffline) {
                newsDetail = GetNewsDetailService.getNewsDetail(key);
                if (newsDetail == null) {
                    return Result.failure();
                } else {
                    return Result.success(newsDetail);
                }
            } else {
                //Log.e("getting offline details", "getOffline");
                newsDetail = GetNewsDetailService.getOfflineNewsDetail(key);
                if (newsDetail == null) {
                    return Result.failure();
                } else {
                    return Result.success(newsDetail);
                }
            }
        } catch (NewsException e) {
            // TODO(bellasong): Add logic to get offline news.
            Log.e(e.getErrorCode(), e.getMessage());
            return Result.failure();
        }
    }
}
