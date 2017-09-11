package com.java.no16.service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.java.no16.protos.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service which reads data from file to cache at the start of app, stores cache data, provides cache data
 * and prints cache data to file when the app terminates.
 */
public class CacheService {

    public static final String NIGHT = "night";
    public static final String SHOW_PICTURE = "showPicture";
    public static final String KEYWORDS = "keywords";

    /** List of all categories. */
    private static final List<Category> allCategoryList = Arrays.asList(Category.values());

    /** List of categories which user needs. */
    private static List<Category> categoryList;

    private static boolean night;
    private static boolean showPicture;
    private static List<String> keywords;

    /**
     * Initiates CacheService.
     * Please execute this method when starts the app.
     * */
    public static void initService(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        night = prefs.getBoolean(NIGHT, false);
        showPicture = prefs.getBoolean(SHOW_PICTURE, true);
        String keywordsString = prefs.getString(KEYWORDS, "");
        if (keywordsString.trim().isEmpty()) keywords = new ArrayList<>();
        else keywords = Arrays.asList(keywordsString.trim().split(";"));
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
        CacheService.categoryList = categoryList;
    }

    public static boolean getFavorite(String newsId) {
        //TODO(bellasong)
        return false;
    }

    public static void setFavorite(String newsId, boolean favorite) {
        //TODO(bellasong)
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

    public static List<String> getKeywordList() {
        return keywords;
    }

    public static void setKeywordList(List<String> keywords) {
        CacheService.keywords = keywords;
    }

    private static String getKeywordListString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String keyword : keywords) {
            stringBuilder.append(keyword);
        }
        return stringBuilder.toString();
    }
}
