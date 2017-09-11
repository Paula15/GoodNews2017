package com.java.no16.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
    public static @Nullable List<SimpleNews> getNewsList(int pageNo, int pageSize, Category category) {
        List<SimpleNews> newsList;
        if (category == Category.ALL) {
            try {
                newsList = newsListHttpService.getNewsList(pageNo, pageSize).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                Log.e(NewsException.CONVERT_FROM_STRING_TO_JSON_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsList", SERVICE_NAME));
                return null;
            }
        } else {
            try {
                newsList = newsListHttpService.getNewsListByCategory(pageNo, pageSize, category.ordinal()).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                Log.e(NewsException.CONVERT_FROM_STRING_TO_JSON_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsList", SERVICE_NAME));
                return null;
            }
        }
        //TODO(wenj): Some image query provides non-exist url.
        //TODO(wenj): Some query takes long time.
        for (SimpleNews simpleNews : newsList) {
            simpleNews.separateImageUrl();
            if (simpleNews.getImageUrl() == null) {
                try {
                    simpleNews.setImageUrl(
                            newsListHttpService.getMissedImage("resultjsonavatarnew", "utf-8", simpleNews.getTitle(), 0, 1).execute().body().getUrl());
                } catch (IOException e) {
                    Log.e(NewsException.GET_IMAGE_ERROR, String.format(NewsException.GET_IMAGE_MESSAGE, simpleNews.getTitle()));
                }
            }
        }
        return newsList;
    }

    /** Gets categories in user's setting. */
    public static List<Category> getCategoryList() {
        return CacheService.getCategoryList();
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
}
