package com.java.no16.service;

import com.java.no16.protos.Category;

import java.util.Arrays;
import java.util.List;

/**
 * Service which reads data from file to cache at the start of app, stores cache data, provides cache data
 * and prints cache data to file when the app terminates.
 */
public class CacheService {

    /** List of all categories. */
    private static final List<Category> allCategoryList = Arrays.asList(Category.values());

    /** List of categories which user needs. */
    private static List<Category> categoryList;

    /**
     * Initiates CacheService.
     * Please execute this method when starts the app.
     * */
    public static void initService() {
        //TODO(bellasong): load data from file to cache.
    }

    /** Gets all categories. */
    public static List<Category> getAllCategoryList() {
        //TODO(bellasong)
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
}
