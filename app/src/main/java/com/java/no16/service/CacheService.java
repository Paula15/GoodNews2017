package com.java.no16.service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.java.no16.database.DBManager;
import com.java.no16.protos.Category;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service which reads data from file to cache at the start of app, stores cache data, provides cache data
 * and prints cache data to file when the app terminates.
 */
public class CacheService {

    public static final String NIGHT = "night";
    public static final String SHOW_PICTURE = "showPicture";
    public static final String KEYWORDS = "keywords";
    public static final String CATEGORIES = "categories";

    /** List of all categories. */
    private static final List<Category> allCategoryList = Arrays.asList(Category.values());

    /** List of categories which user needs. */
    private static List<Category> categoryList;

    /** Map storing newsIds and their corresponding favorite status which have been loading in cache. */
    private static Map<String, Boolean> favoriteStatus;

    private static Map<String, Boolean> markStatus;

    private static boolean night;
    private static boolean showPicture;
    private static List<String> keywords;
    private static CacheService cacheService;
    private static DBManager dbManager;

    /**
     * Initiates CacheService.
     * Please execute this method when starts the app.
     * */
    public static void initService(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        night = prefs.getBoolean(NIGHT, false);
        showPicture = prefs.getBoolean(SHOW_PICTURE, true);
        String keywordsString = prefs.getString(KEYWORDS, "");
        String categoryString = prefs.getString(CATEGORIES, "ALL,TECHNOLOGY,EDUCATION,MILITARY");
        keywords = new ArrayList<>();
        if (!keywordsString.trim().isEmpty()) {
            keywords = new ArrayList<>(Arrays.asList(keywordsString.trim().split(",")));
        }
        categoryList = new ArrayList<>();
        for (String str : Arrays.asList(categoryString.trim().split(","))) {
            if (!str.isEmpty()) categoryList.add(Category.valueOf(str.trim()));
        }
        favoriteStatus = new HashMap<>();
        markStatus = new HashMap<>();
        dbManager = new DBManager(activity);
    }

    /**
     * Close CacheService.
     * Please execute this method when close the app.
     * */
    public static void closeService(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putBoolean(NIGHT, night).apply();
        prefs.edit().putBoolean(SHOW_PICTURE, showPicture).apply();
        prefs.edit().putString(KEYWORDS, getKeywordListString()).apply();
        prefs.edit().putString(CATEGORIES, getCategoryListString()).apply();
        dbManager.closeDB(favoriteStatus);
    }

    /** Gets all categories. */
    public static List<Category> getAllCategoryList() {
        return allCategoryList;
    }

    /** Gets categories in user's setting. */
    public static List<Category> getCategoryList() {
        return categoryList;
    }

    public static void setCategoryList(List<Category> categoryList) {
        CacheService.categoryList = new ArrayList<>(categoryList);
    }

    public static boolean getFavorite(String newsId) {
        if (!favoriteStatus.containsKey(newsId)) {
            favoriteStatus.put(newsId, dbManager.queryFavorite(newsId));
        }
        return favoriteStatus.get(newsId);
    }

    public static void setFavorite(String newsId, boolean favorite) {
        favoriteStatus.put(newsId, favorite);
    }

    public static synchronized boolean getMark(String newsId) {
        if (!markStatus.containsKey(newsId)) {
            Log.e("getmark", "start");
            markStatus.put(newsId, dbManager.queryExist(newsId));
            Log.e("getmark", "end");
        }
        return markStatus.get(newsId);
    }

    public static synchronized List<SimpleNews> getFavoriteList(int pageNo, int pageSize, Category category) throws NewsException {
        DBManager.updateFavorite(favoriteStatus);
        return DBManager.queryFavoriteList(pageNo, pageSize, category);
    }

    public static void setMark(String newsId) {
        markStatus.put(newsId, true);
    }

    public static boolean isNight() {
        return night;
    }

    public static void setNight(boolean night) {
        CacheService.night = night;
    }

    public static boolean isShowPicture() {
        return showPicture;
    }

    public static void setShowImage(boolean showPicture) {
        CacheService.showPicture = showPicture;
    }

    public static void addKeywords(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        keywords.addAll(new ArrayList<String>(Arrays.asList(keyword.split(";"))));
    }

    public static List<String> getKeywordList() {
        return keywords;
    }

    public static void setKeywordList(List<String> keywords) {
        CacheService.keywords = new ArrayList<>(keywords);
    }

    public static CacheService get() {
        if (cacheService == null) {
            cacheService = new CacheService();
        }
        return cacheService;
    }

    public static void storeNewsDetail(NewsDetail newsDetail) {
        markStatus.put(newsDetail.getNewsId(), true);
        dbManager.add(newsDetail);
    }

    public static List<SimpleNews> getOfflineNewsList(int pageNo, int pageSize, Category category) throws NewsException {
        return dbManager.queryNewsList(pageNo, pageSize, category);
    }

    public static NewsDetail getOfflineNewsDetail(String newsId) throws NewsException {
        return dbManager.queryNewsDetail(newsId);
    }

    private static String getKeywordListString() {
        String listString = keywords.toString();
        return listString.substring(1, listString.length() - 1);
    }

    private static String getCategoryListString() {
        String listString = categoryList.toString();
        return listString.substring(1, listString.length() - 1);
    }
}
