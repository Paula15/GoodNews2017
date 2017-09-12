package com.java.no16.ui.newslist;

import com.google.android.agera.BaseObservable;
import com.java.no16.protos.Category;
import com.java.no16.supplier.NewsListSupplier;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListObservable extends BaseObservable {

    NewsListSupplier supplier;

    public NewsListObservable(NewsListSupplier supplier) {
        this.supplier = supplier;
    }

    public void refreshNews(int pageNo, int pageSize, Category category) {
        supplier.setPageNo(pageNo);
        supplier.setPageSize(pageSize);
        supplier.setCategory(category);
        dispatchUpdate();
    }
}
