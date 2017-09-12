package com.java.no16.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.NestedScrollingChild;
import android.util.Log;

import com.java.no16.protos.Category;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;

/** Class executing database related operations. */
public class DBManager {
    private static DBHelper helper;
    private static SQLiteDatabase db;
    private static DBManager instance;

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    /**
     * Adds news detail {@link com.java.no16.protos.NewsDetail}
     */
    public static void add(NewsDetail newsDetail) {
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

    public static List<SimpleNews> queryNewsList(int pageNo, int pageSize, Category category) throws NewsException {
        db = helper.getWritableDatabase();
        List<SimpleNews> newsList = new ArrayList<>();
        Cursor c;
        if (category == Category.ALL) c = db.rawQuery("SELECT * FROM news", null);
        else c = db.rawQuery("SELECT * FROM news WHERE category = ?", new String[]{category.getName()});
        Log.e(c.getCount() + "", pageNo + "");
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

    public static NewsDetail queryNewsDetail(String newsId) throws NewsException {
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

    public static boolean queryFavorite(String newsId) {
        db = helper.getWritableDatabase();
        Log.e("queryFavorite0", newsId);
        Cursor c = db.rawQuery("SELECT * FROM news WHERE id = ?", new String[]{newsId});
        Log.e("queryFavorite", c.getCount() + "");
        if (c.getCount() == 0) {
            return false;
        }
        boolean ans = (c.getInt(c.getColumnIndex("favorite")) == 1);
        Log.e("queryFavorite2", ans + "");
        c.close();
        db.close();
        return ans;
    }

    /**
     * close database
     */
    public static void closeDB(Map<String, Boolean> favoriteStatus) {
        db = helper.getWritableDatabase();
        for (String newsId : favoriteStatus.keySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("favorite", favoriteStatus.get(newsId) ? 1 : 0);
            db.update("news", contentValues, "id='" + newsId + "'", null);
        }
        db.close();
    }
}
