package com.java.no16.ui.newsdetail;

import com.google.android.agera.BaseObservable;
import com.java.no16.supplier.NewsDetailSupplier;

/**
 * Created by songshihong on 11/09/2017.
 */

public class NewsDetailObservable extends BaseObservable {

    NewsDetailSupplier supplier;

    public NewsDetailObservable(NewsDetailSupplier supplier) {
        this.supplier = supplier;
    }

    public void refreshNews() {
        dispatchUpdate();
    }
}
