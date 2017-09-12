package com.java.no16.supplier;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.service.CacheService;

/**
 * Created by songshihong on 12/09/2017.
 */

public class SettingSupplier implements Supplier<Result<CacheService>> {

    @NonNull
    @Override
    public Result<CacheService> get() {
        return Result.success(CacheService.get());
    }
}
