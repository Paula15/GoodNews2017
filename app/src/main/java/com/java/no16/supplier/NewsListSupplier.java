package com.java.no16.supplier;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.Category;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.GetNewsListService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListSupplier implements Supplier<Result<List<SimpleNews>>> {

    @NonNull
    @Override
    public Result<List<SimpleNews>> get() {
        List<SimpleNews> list = getNewsList();
        if (list == null) {
            return Result.failure();
        } else {
            return Result.success(getNewsList());
        }
    }

    private List<SimpleNews> getNewsList() {
        return GetNewsListService.getNewsList(1, 20, Category.ALL);
    }
}
