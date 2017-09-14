package com.java.no16.supplier;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.Category;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.GetNewsListService;
import com.java.no16.service.GetSearchResultService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListSupplier implements Supplier<Result<List<SimpleNews>>> {
    int pageNo = 1, pageSize = 20;
    Category category = Category.ALL;
    String searchKey = "";
    boolean isFavoriteMode = false;

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
            List<SimpleNews> ret;
            if (isFavoriteMode == false) {
                if (searchKey.isEmpty()) {
                    ret = GetNewsListService.getNewsList(pageNo, pageSize, category);
                } else {
                    ret = GetSearchResultService.getSearchResult(searchKey, pageNo, pageSize, category);
                }
            } else {
                ret = GetNewsListService.getFavoriteList(pageNo, pageSize, category);
            }
            //Log.e("@" + Thread.currentThread().getName() + " => " + category.getName(), "NewsListSupplier.getNewsList");
            return ret;
        } catch (NewsException e) {
            return null;
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

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setFavoriteMode(boolean favoriteMode) {
        isFavoriteMode = favoriteMode;
    }

    public boolean getFavoriteMode() {
        return isFavoriteMode;
    }
}
