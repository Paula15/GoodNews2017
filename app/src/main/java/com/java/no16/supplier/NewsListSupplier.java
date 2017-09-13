package com.java.no16.supplier;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.Category;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.GetNewsListService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListSupplier implements Supplier<Result<List<SimpleNews>>> {
    int pageNo = 1, pageSize = 20;
    Category category = Category.ALL;

    @NonNull
    @Override
    public Result<List<SimpleNews>> get() {
        List<SimpleNews> list = getNewsList();
        if (list == null) {
            return Result.failure();
        } else {
            return Result.success(list);
        }
    }

    private List<SimpleNews> getNewsList() {
        try {
            Log.e("error", "getNewsList");
            return GetNewsListService.getNewsList(pageNo, pageSize, category);
        } catch (NewsException e) {
            Log.e(e.getErrorCode(), e.getMessage());
            // TODO(zpzhou): Add logic to get offline news.
            return new ArrayList<SimpleNews>();
        }
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
