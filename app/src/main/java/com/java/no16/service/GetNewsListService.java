package com.java.no16.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.no16.protos.Category;
import com.java.no16.protos.ImageUrlJsonParser;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;
import com.java.no16.protos.SimpleNewsList;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service providing news list view with corresponding data.
 */
public class GetNewsListService {

    interface NewsListHttpService {
        @GET("action/query/latest")
        @ConverterType("SimpleNewsList")
        Call<SimpleNewsList> getNewsList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

        @GET("action/query/latest")
        @ConverterType("SimpleNewsList")
        Call<SimpleNewsList> getNewsListByCategory(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("category") int category);

        @GET("https://image.baidu.com/search/avatarjson")
        @ConverterType("String")
        Call<ImageUrlJsonParser> getMissedImage(@Query("tn") String tn, @Query("ie") String ie, @Query("word") String keyword, @Query("pn") int pn, @Query("rn") int rn);
    }

    private static String SERVICE_NAME = "GetNewsListService";

    private static NewsListHttpService newsListHttpService;

    /**
     * Initiates GetNewsListService.
     * Please execute this method when starts the app.
     * */
    public static void initService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://166.111.68.66:2042/news/")
                .addConverterFactory(new Converter.Factory() {
                    Gson gson = new Gson();

                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, Retrofit retrofit) {
                        return new Converter<ResponseBody, Object>() {
                            @Override
                            public Object convert(ResponseBody value) throws IOException {
                                try {
                                    return gson.getAdapter(TypeToken.get(type)).fromJson(value.charStream());
                                } finally {
                                    value.close();
                                }
                            }
                        };
                    }
                }).build();
        newsListHttpService = retrofit.create(NewsListHttpService.class);
    }

    /** Gets news list according to providing pageNo, pageSize, category. */
    public static synchronized @Nullable List<SimpleNews> getNewsList(int pageNo, int pageSize, Category category) throws NewsException {
        List<SimpleNews> newsList;
        if (category == Category.ALL) {
            try {
                newsList = newsListHttpService.getNewsList(pageNo, pageSize).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                throw new NewsException(NewsException.NEWS_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsList", SERVICE_NAME));
            }
        } else {
            try {
                newsList = newsListHttpService.getNewsListByCategory(pageNo, pageSize, category.ordinal()).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                throw new NewsException(NewsException.NEWS_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsList", SERVICE_NAME));
            }
        }

        for (SimpleNews simpleNews : newsList) {
            simpleNews.separateImageUrl();
            simpleNews.setMark(CacheService.getMark(simpleNews.getNewsId()));
        }
        Log.e("@" + Thread.currentThread().getName() + " => " + category.getName(), "GetNewsListService.getNewsList");
        return newsList;
    }

    public static @Nullable List<SimpleNews> getOfflineNewsList(int pageNo, int pageSize, Category category) throws NewsException {
        return CacheService.getOfflineNewsList(pageNo, pageSize, category);
    }

    /** Gets categories in user's setting. */
    public static List<Category> getCategoryList() {
        return new ArrayList<>(CacheService.getCategoryList());
    }

    /** Gets categories not in user's setting. */
    public static List<Category> getUnusedCategoryList() {
        List<Category> categories = CacheService.getCategoryList();
        List<Category> unusedCategories = new ArrayList<>();
        for (Category category : CacheService.getAllCategoryList()) {
            if (!categories.contains(category)) {
                unusedCategories.add(category);
            }
        }
        return unusedCategories;
    }

    /** Sets categories to user's setting. */
    public static void setCategoryList(List<Category> categories) {
        CacheService.setCategoryList(categories);
    }

    /** Gets missing image with specified title. */
    public static String getMissedImage(String title) {
        try {
            return newsListHttpService.getMissedImage("resultjsonavatarnew", "utf-8", title, 0, 1).execute().body().getUrl();
        } catch (IOException e) {
            Log.e(NewsException.GET_IMAGE_ERROR, String.format(NewsException.GET_IMAGE_MESSAGE, title));
            return "";
        }
    }
}
