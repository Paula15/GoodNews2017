package com.java.no16.supplier;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.java.no16.protos.SimpleNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListSupplier implements Supplier<Result<List<SimpleNews>>> {

    @NonNull
    @Override
    public Result<List<SimpleNews>> get() {
        return Result.success(getNewsList());
    }

    private List<SimpleNews> getNewsList() {
        List<SimpleNews> ret = new ArrayList<SimpleNews>();
        ret.add(
            new SimpleNews(
                "201608100421f2d8cf63b03d431eb847d4b3e7af8f24",
                "Prisma爆红这么久 现在才有中国追随者",
                "第一财经日报",
                "Aug 9, 2016 12:00:00 AM",
                "对于大多数Prisma用户而言，最大的不满依然来自于图片处理的时间太长，一...",
                "http://upload.qianlong.com/2016/0809/1470711910844.jpg",
                false
        ));
        ret.add(
            new SimpleNews(
                "12345",
                "Title1",
                "Author1",
                "20170906",
                "Description1",
                "https://pic3.zhimg.com/v2-6d7ddd1a1fdd54e851c9a4a208e2945e.jpg",
                 false
            )
        );
        return ret;
    }
}
