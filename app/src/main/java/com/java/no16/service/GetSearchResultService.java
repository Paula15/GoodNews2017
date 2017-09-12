package com.java.no16.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.no16.protos.Category;
import com.java.no16.protos.ImageUrlJsonParser;
import com.java.no16.protos.NewsLoggerUtil;
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
 * Service providing search view with corresponding data.
 */
public class GetSearchResultService {
    interface SearchResultHttpService {
        @GET("action/query/search")
        @ConverterType("SimpleNewsList")
        Call<SimpleNewsList> getSearchResult(@Query("keyword") String keyword, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

        @GET("action/query/search")
        @ConverterType("SimpleNewsList")
        Call<SimpleNewsList> getSearchResultByCategory(@Query("keyword") String keyword, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("category") int category);

        @GET("https://image.baidu.com/search/avatarjson")
        @ConverterType("String")
        Call<ImageUrlJsonParser> getMissedImage(@Query("tn") String tn, @Query("ie") String ie, @Query("word") String keyword, @Query("pn") int pn, @Query("rn") int rn);
    }

    private static String SERVICE_NAME = "GetSearchResultService";

    private static GetSearchResultService.SearchResultHttpService searchResultHttpService;

    /**
     * Initiates GetSearchResultService.
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
        searchResultHttpService = retrofit.create(GetSearchResultService.SearchResultHttpService.class);
    }

    /** Gets news list according to providing pageNo, pageSize, category. */
    public static @Nullable List<SimpleNews> getSearchResult(String keyword, int pageNo, int pageSize, Category category) {
        List<SimpleNews> searchResult;
        if (category == Category.ALL) {
            try {
                searchResult = searchResultHttpService.getSearchResult(keyword, pageNo, pageSize).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                Log.e(NewsLoggerUtil.CONVERT_FROM_STRING_TO_JSON_ERROR, String.format(NewsLoggerUtil.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getSearchResult", SERVICE_NAME));
                return null;
            }
        } else {
            try {
                searchResult = searchResultHttpService.getSearchResultByCategory(keyword, pageNo, pageSize, category.ordinal()).execute().body().getSimpleNewsList();
            } catch (IOException e) {
                Log.e(NewsLoggerUtil.CONVERT_FROM_STRING_TO_JSON_ERROR, String.format(NewsLoggerUtil.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getSearchResult", SERVICE_NAME));
                return null;
            }
        }
        for (SimpleNews simpleNews : searchResult) {
            simpleNews.separateImageUrl();
        }
        return searchResult;
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

    /** Gets missing image with specified title. */
    public static String getMissedImage(String title) {
        try {
            return searchResultHttpService.getMissedImage("resultjsonavatarnew", "utf-8", title, 0, 1).execute().body().getUrl();
        } catch (IOException e) {
            Log.e(NewsLoggerUtil.GET_IMAGE_ERROR, String.format(NewsLoggerUtil.GET_IMAGE_MESSAGE, title));
            return "";
        }
    }
}
