package com.java.no16.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.ImageUrlJsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service providing news detail view with corresponding data.
 */

public class GetNewsDetailService {

    interface NewsDetailHttpService {
        @GET("action/query/detail")
        @ConverterType("NewsDetail")
        Call<NewsDetail> getNewsDetail(@Query("newsId") String newsId);

        @GET("https://image.baidu.com/search/avatarjson")
        @ConverterType("String")
        Call<ImageUrlJsonParser> getMissedImage(@Query("tn") String tn, @Query("ie") String ie, @Query("word") String keyword, @Query("pn") int pn, @Query("rn") int rn);
    }

    private static String SERVICE_NAME = "GetNewsDetailService";

    private static GetNewsDetailService.NewsDetailHttpService newsdetailHttpService;

    /**
     * Initiates GetNewsDetailService.
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
        newsdetailHttpService = retrofit.create(GetNewsDetailService.NewsDetailHttpService.class);
    }

    /** Gets news detail with newsId. */
    public static synchronized NewsDetail getNewsDetail(String newsId) throws NewsException {
        NewsDetail newsDetail;
        try {
            newsDetail = newsdetailHttpService.getNewsDetail(newsId).execute().body();
        } catch (IOException e) {
            throw  new NewsException(NewsException.NEWS_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsDetail", SERVICE_NAME));
        }
        newsDetail.setFavorite(CacheService.getFavorite(newsId));
        newsDetail.separateImageUrlString();
        newsDetail.injectHyperlink();
        CacheService.storeNewsDetail(newsDetail);
        return newsDetail;
    }

    public static synchronized NewsDetail getOfflineNewsDetail(String newsId) throws NewsException {
        return CacheService.getOfflineNewsDetail(newsId);
    }

    /** Gets missing image with specified title. */
    public static String getMissedImage(String title) {
        try {
            return newsdetailHttpService.getMissedImage("resultjsonavatarnew", "utf-8", title, 0, 1).execute().body().getUrl();
        } catch (IOException e) {
            Log.e(NewsException.GET_IMAGE_ERROR, String.format(NewsException.GET_IMAGE_MESSAGE, title));
            return "";
        }
    }

    public static void setFavorite(String newsId, boolean favorite) {
        CacheService.setFavorite(newsId, favorite);
    }

    public static synchronized List<Bitmap> getImages(List<String> urls) {
        List<Bitmap> images = new ArrayList<>();
        for (String url : urls) {
            try {
                images.add(getImage(url));
            } catch (NewsException e) {
                Log.e(e.getErrorCode(), e.getMessage());
            }
        }
        return images;
    }

    public static synchronized Bitmap getImage(String path) throws NewsException {
        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            throw new NewsException(NewsException.NETWORK_ERROR, String.format(NewsException.IMAGE_NOT_FOUND, path));
        }
        try {
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } else {
                throw new NewsException(NewsException.NETWORK_ERROR, String.format(NewsException.IMAGE_NOT_FOUND, path));
            }
        } catch (IOException e) {
            throw new NewsException(NewsException.NETWORK_ERROR, String.format(NewsException.IMAGE_NOT_FOUND, path));
        }
    }
}
