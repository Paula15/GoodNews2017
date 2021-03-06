package com.java.no16.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.java.no16.protos.Category;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Class executing database related operations. */
public class DBManager {
    private static DBHelper helper;
    private static SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    /**
     * Adds news detail {@link com.java.no16.protos.NewsDetail}
     */
    public static synchronized void add(NewsDetail newsDetail) {
        db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT OR REPLACE INTO news VALUES(?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{newsDetail.getNewsId(),
                            newsDetail.getCategoryString(),
                            newsDetail.getTitle(),
                            newsDetail.getAuthor(),
                            newsDetail.getContent(),
                            newsDetail.getDate(),
                            newsDetail.isFavorite() ? 1 : 0});
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(NewsException.FAIL_IN_STORE_NEWS, e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public static synchronized List<SimpleNews> queryNewsList(int pageNo, int pageSize, Category category) throws NewsException {
        db = helper.getWritableDatabase();
        List<SimpleNews> newsList = new ArrayList<>();
        Cursor c;
        if (category == Category.ALL) c = db.rawQuery("SELECT * FROM news", null);
        else c = db.rawQuery("SELECT * FROM news WHERE category = ?", new String[]{category.getName()});
        if (c.getCount() <= (pageNo - 1) * pageSize) {
            throw new NewsException(NewsException.NEWS_ERROR, NewsException.INDEX_OUT_OF_BOUND_MESSAGE);
        }
        c.moveToPosition((pageNo - 1) * pageSize);
        while (c.moveToNext() && newsList.size() <= pageSize) {
            SimpleNews news = new SimpleNews(c.getString(c.getColumnIndex("id")), c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("author")), c.getString(c.getColumnIndex("date")),
                    c.getString(c.getColumnIndex("content")), "", true);
            if (news.getDescription().length() > 50) news.setDescription(news.getDescription().substring(0, 50));
            newsList.add(news);
        }
        c.close();
        db.close();
        return newsList;
    }

    public static synchronized NewsDetail queryNewsDetail(String newsId) throws NewsException {
        db = helper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM news WHERE id = ?", new String[]{newsId});
        if (c.getCount() == 0) {
            throw new NewsException(NewsException.NEWS_ERROR, String.format(NewsException.NEWS_ID_NOT_EXIST_MESSAGE, newsId));
        }
        c.moveToFirst();
        NewsDetail newsDetail = new NewsDetail(c.getString(c.getColumnIndex("id")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("author")),
                c.getString(c.getColumnIndex("date")), c.getString(c.getColumnIndex("content")), c.getInt(c.getColumnIndex("favorite")) == 1);
        c.close();
        db.close();
        return newsDetail;
    }

    public static synchronized boolean queryExist(String newsId) {
        db = helper.getWritableDatabase();
//        Log.e("begin", "begin");
        Cursor c = db.rawQuery("SELECT * FROM news WHERE id = ?", new String[]{newsId});
//        Log.e("Query", "queryExist: " + Thread.currentThread().toString());
//        Log.e("end", "end");
        boolean exist = (c.getCount() > 0);
        c.close();
        db.close();
        return exist;
    }

    public static synchronized boolean queryFavorite(String newsId) {
        db = helper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM news WHERE id = ?", new String[]{newsId});
        if (c.getCount() == 0) {
            return false;
        }
        c.moveToFirst();
        boolean ans = (c.getInt(c.getColumnIndex("favorite")) == 1);
        c.close();
        db.close();
        return ans;
    }

    public static synchronized List<SimpleNews> queryFavoriteList(int pageNo, int pageSize, Category category) throws NewsException {
        db = helper.getWritableDatabase();
        List<SimpleNews> newsList = new ArrayList<>();
        Cursor c;
        if (category == Category.ALL) c = db.rawQuery("SELECT * FROM news WHERE favorite = ?", new String[]{Integer.toString(1)});
        else c = db.rawQuery("SELECT * FROM news WHERE category = ? AND favorite = ?", new String[]{category.getName(), Integer.toString(1)});
        //Log.e(c.getCount() + "", pageNo + "");
        if (c.getCount() <= (pageNo - 1) * pageSize) {
            throw new NewsException(NewsException.NEWS_ERROR, NewsException.INDEX_OUT_OF_BOUND_MESSAGE);
        }
        c.moveToPosition((pageNo - 1) * pageSize);
        c.moveToPrevious();
        while (c.moveToNext() && newsList.size() <= pageSize) {
            SimpleNews news = new SimpleNews(c.getString(c.getColumnIndex("id")), c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("author")), c.getString(c.getColumnIndex("date")),
                    c.getString(c.getColumnIndex("content")), "", true);
            if (news.getDescription().length() > 50) news.setDescription(news.getDescription().substring(0, 50));
            newsList.add(news);
        }
        c.close();
        db.close();
        return newsList;
    }

    public static synchronized void updateFavorite(Map<String, Boolean> favoriteStatus) {
        db = helper.getWritableDatabase();
        //Log.e("updateFavorite", "begin");
        for (String newsId : favoriteStatus.keySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("favorite", favoriteStatus.get(newsId) ? 1 : 0);
            db.update("news", contentValues, "id='" + newsId + "'", null);
        }
        //Log.e("updateFavorite", "end");
        db.close();
    }

    /** Closes database. */
    public static void closeDB(Map<String, Boolean> favoriteStatus) {
        updateFavorite(favoriteStatus);
    }
}
